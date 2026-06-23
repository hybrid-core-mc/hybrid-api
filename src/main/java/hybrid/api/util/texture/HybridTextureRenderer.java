package hybrid.api.util.texture;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.*;

public class HybridTextureRenderer {
    private static final RenderPipeline TEXTURE_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                          .withLocation(Identifier.fromNamespaceAndPath("hybrid-api", "pipeline/texture"))
                          .withFragmentShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/texture"))
                          .withVertexShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/text"))
                          .withBlend(BlendFunction.TRANSLUCENT)
                          .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                          .withUniform("FontUniforms", UniformType.UNIFORM_BUFFER)
                          .build()
    );

    public static final List<TextureCommand> DRAW_LIST = new ArrayList<>();

    private record TextureCommand(
            HybridTexture texture,
            float x, float y, float width, float height,
            float u0, float v0, float u1, float v1,
            int color, boolean linear 
    ) {}

    private final List<TextureCommand> localTextures = new ArrayList<>();

    
    public void drawTexture(HybridTexture texture, float x, float y, float width, float height, int color, boolean linear) {
        localTextures.add(new TextureCommand(texture, x, y, width, height, 0.0f, 0.0f, 1.0f, 1.0f, color, linear));
    }

    
    public void drawTextureUV(HybridTexture texture, float x, float y, float width, float height,
                              float u0, float v0, float u1, float v1, int color, boolean linear) {
        localTextures.add(new TextureCommand(texture, x, y, width, height, u0, v0, u1, v1, color, linear));
    }

    
    public void drawTextureSubRegion(HybridTexture texture, float x, float y, float width, float height,
                                     int sourceX, int sourceY, int sourceWidth, int sourceHeight, int color, boolean linear) {
        float u0 = (float) sourceX / texture.width;
        float v0 = (float) sourceY / texture.height;
        float u1 = (float) (sourceX + sourceWidth) / texture.width;
        float v1 = (float) (sourceY + sourceHeight) / texture.height;

        localTextures.add(new TextureCommand(texture, x, y, width, height, u0, v0, u1, v1, color, linear));
    }

    public void flush() {
        DRAW_LIST.addAll(localTextures);
        localTextures.clear();
    }

    public static void flushAll() {
        if (DRAW_LIST.isEmpty()) return;

        RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

        GpuBufferSlice transformSlice = RenderSystem.getDynamicUniforms().writeTransform(
                new Matrix4f().setTranslation(0, 0, -11000),
                new Vector4f(1, 1, 1, 1), new Vector3f(), new Matrix4f()
        );

        for (TextureCommand cmd : DRAW_LIST) {
            
            FilterMode filterMode = cmd.linear() ? FilterMode.LINEAR : FilterMode.NEAREST;
            GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(filterMode);

            drawTextureQuad(renderTarget, transformSlice, sampler, cmd.texture().view,
                    cmd.x(), cmd.y(), cmd.width(), cmd.height(),
                    cmd.u0(), cmd.v0(), cmd.u1(), cmd.v1(), cmd.color());
        }

        DRAW_LIST.clear();
    }

    private static void drawTextureQuad(RenderTarget renderTarget, GpuBufferSlice dynamicTransforms,
                                        GpuSampler sampler, GpuTextureView textureView,
                                        float x, float y, float width, float height,
                                        float u0, float v0, float u1, float v1, int color) {
        ByteBuffer vertexBuffer = MemoryUtil.memAlloc(4 * 24);
        try {
            vertexTexture(vertexBuffer, x,         y,          u0, v0, color);
            vertexTexture(vertexBuffer, x,         y + height, u0, v1, color);
            vertexTexture(vertexBuffer, x + width, y + height, u1, v1, color);
            vertexTexture(vertexBuffer, x + width, y,          u1, v0, color);
            vertexBuffer.flip();

            GpuBuffer vertexBufferObj = RenderSystem.getDevice().createBuffer(
                    () -> "BRender texture vertices",
                    GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
                    vertexBuffer.remaining()
            );
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(vertexBufferObj.slice(), vertexBuffer);

            RenderSystem.AutoStorageIndexBuffer indexStorage =
                    RenderSystem.getSequentialBuffer(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS);
            GpuBuffer indexBufferObj = indexStorage.getBuffer(6);

            try (RenderPass renderPass = RenderSystem.getDevice()
                                                     .createCommandEncoder()
                                                     .createRenderPass(
                                                             () -> "BRender texture",
                                                             renderTarget.getColorTextureView(),
                                                             OptionalInt.empty(),
                                                             renderTarget.useDepth ? renderTarget.getDepthTextureView() : null,
                                                             OptionalDouble.empty()
                                                     )) {
                renderPass.setPipeline(TEXTURE_PIPELINE);
                RenderSystem.bindDefaultUniforms(renderPass);
                renderPass.setUniform("DynamicTransforms", dynamicTransforms);
                renderPass.bindTexture("Sampler0", textureView, sampler);
                renderPass.setVertexBuffer(0, vertexBufferObj);
                renderPass.setIndexBuffer(indexBufferObj, indexStorage.type());
                renderPass.drawIndexed(0, 0, 6, 1);
            }
            vertexBufferObj.close();
        } finally {
            MemoryUtil.memFree(vertexBuffer);
        }
    }

    private static void vertexTexture(ByteBuffer buffer, float x, float y, float u, float v, int color) {
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putFloat(1.0f);
        buffer.putFloat(u);
        buffer.putFloat(v);

        buffer.put((byte) ((color >> 16) & 0xFF));
        buffer.put((byte) ((color >> 8)  & 0xFF));
        buffer.put((byte) (color         & 0xFF));
        buffer.put((byte) ((color >> 24) & 0xFF));
    }
}