package hybrid.api.util.font.fancy;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import static hybrid.api.Main.mc;

public class FontRenderer {
    public static final RenderPipeline TEXT_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                          .withLocation(Identifier.fromNamespaceAndPath("hybrid-api", "pipeline/text"))
                          .withFragmentShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/text"))
                          .withVertexShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/text"))
                          .withBlend(BlendFunction.TRANSLUCENT)
                          .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                          .withUniform("FontUniforms", UniformType.UNIFORM_BUFFER) 
                          .build()
    );
    private static final List<TextCommand> COMMAND_QUEUE = new ArrayList<>();
    private static final int VERTEX_STRIDE_BYTES = 24;

    public static void flushAll() {
        if (COMMAND_QUEUE.isEmpty()) return;

        for (TextCommand command : COMMAND_QUEUE) {
            renderTextCommand(command);
        }

        COMMAND_QUEUE.clear();
        Uniform.clear(); 
    }

    private static void renderTextCommand(TextCommand cmd) {
        if (cmd.font() == null) return;

        RenderTarget fbo = mc.getMainRenderTarget();

        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms()
                                                       .writeTransform(
                                                               new Matrix4f().setTranslation(0.0F, 0.0F, -11000.0F),
                                                               new Vector4f(1.0F, 1.0F, 1.0F, 1.0F),
                                                               new Vector3f(),
                                                               new Matrix4f()
                                                       );

        GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);

        float[][] quads = cmd.bakedQuads() != null
                ? cmd.bakedQuads()
                : cmd.font().getQuads(cmd.text(), cmd.x(), cmd.y(), cmd.size());

        int vertexCount = quads.length * 4;
        ByteBuffer vertexBuffer = MemoryUtil.memAlloc(vertexCount * VERTEX_STRIDE_BYTES);

        try {
            for (int i = 0; i < quads.length; i++) {
                float[] quad = quads[i];
                int color = (cmd.charColors() != null && i < cmd.charColors().length) ? cmd.charColors()[i] : cmd.color();

                float x0 = quad[0], y0 = quad[1], x1 = quad[2], y1 = quad[3];
                float u0 = quad[4], v0 = quad[5], x1_u = quad[6], y1_v = quad[7];

                vertex(vertexBuffer, x0, y0, u0, v0, color);
                vertex(vertexBuffer, x0, y1, u0, y1_v, color);
                vertex(vertexBuffer, x1, y1, x1_u, y1_v, color);
                vertex(vertexBuffer, x1, y0, x1_u, v0, color);
            }
            vertexBuffer.flip();

            int indexCount = quads.length * 6;
            RenderSystem.AutoStorageIndexBuffer indexStorage = RenderSystem.getSequentialBuffer(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS);
            GpuBuffer indexBuf = indexStorage.getBuffer(indexCount);

            GpuBuffer vertexBuf = RenderSystem.getDevice().createBuffer(
                    () -> "Hybrid text vertices",
                    GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
                    vertexBuffer.remaining()
            );
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(vertexBuf.slice(), vertexBuffer);

            
            float currentTimeSeconds = (System.currentTimeMillis() % 1000000) / 1000.0f;

            
            GpuBufferSlice fontUniformBuffer = Uniform.STORAGE.writeUniform(buffer -> {
                Std140Builder.intoBuffer(buffer)
                             .putVec4(currentTimeSeconds, 0.0F, 0.0F, 0.0F); 
            });

            assert fbo.getColorTextureView() != null;
            try (RenderPass pass = RenderSystem.getDevice()
                                               .createCommandEncoder()
                                               .createRenderPass(
                                                       () -> "Hybrid text",
                                                       fbo.getColorTextureView(),
                                                       OptionalInt.empty(),
                                                       fbo.useDepth ? fbo.getDepthTextureView() : null,
                                                       OptionalDouble.empty()
                                               )) {
                pass.setPipeline(TEXT_PIPELINE);
                RenderSystem.bindDefaultUniforms(pass);
                pass.setUniform("DynamicTransforms", dynamicTransforms);
                pass.setUniform("FontUniforms", fontUniformBuffer); 
                pass.bindTexture("Sampler0", cmd.font().atlasView, sampler);
                pass.setVertexBuffer(0, vertexBuf);
                pass.setIndexBuffer(indexBuf, indexStorage.type());
                pass.drawIndexed(0, 0, indexCount, 1);
            }

            vertexBuf.close();
        } finally {
            MemoryUtil.memFree(vertexBuffer);

        }

    }

    private static void vertex(ByteBuffer buf, float x, float y, float u, float v, int color) {
        buf.putFloat(x);
        buf.putFloat(y);
        buf.putFloat(0);
        buf.putFloat(u);
        buf.putFloat(v);
        buf.put((byte) ((color >> 16) & 0xFF));
        buf.put((byte) ((color >> 8) & 0xFF));
        buf.put((byte) (color & 0xFF));
        buf.put((byte) ((color >> 24) & 0xFF));
    }

    public void drawText(StyledFont font, String text, float x, float y, float size, int color) {
        COMMAND_QUEUE.add(new TextCommand(font, text, x, y, size, color, false));
    }

    private record TextCommand(StyledFont font, String text, float x, float y, float size, int color, boolean shadow,
                               int[] charColors, float[][] bakedQuads) {
        TextCommand(StyledFont font, String text, float x, float y, float size, int color, boolean shadow) {
            this(font, text, x, y, size, color, shadow, null, null);
        }
    }

    
    public static class Uniform {
        private static final int SIZE = new Std140SizeCalculator()
                .putVec4() 
                .get();

        private static final DynamicUniformStorage<DynamicUniformStorage.DynamicUniform> STORAGE =
                new DynamicUniformStorage<>("Font UBO", SIZE, 4);

        public static void clear() {
            STORAGE.endFrame();
        }
    }
}