package hybrid.api.util.shader;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.fabricmc.fabric.api.client.rendering.v1.SpecialGuiElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class HueShader extends PictureInPictureRenderer<HueShader.@NotNull State> {

    private static RenderPipeline PIPELINE_hue;
    private State prevState;

    protected HueShader(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    public static void init() {
        SpecialGuiElementRegistry.register(context -> new HueShader(context.vertexConsumers()));
        PIPELINE_hue = RenderPipelines.register(
                RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                              .withLocation(Identifier.fromNamespaceAndPath("hybrid-api", "pipeline/hue"))
                              .withFragmentShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/hue"))
                              .withVertexShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/position"))
                              .withBlend(BlendFunction.TRANSLUCENT)
                              .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
                              .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
                              .build());
    }

    @Override
    public @NonNull Class<State> getRenderStateClass() {
        return State.class;
    }

    @Override
    protected boolean textureIsReadyToBlit(State state) {
        return Objects.equals(prevState, state);
    }

    @Override
    protected void renderToTexture(State state, @NonNull PoseStack poseStack) {
        float width = (state.width + 2 * state.outset) * state.scale;
        float height = (state.height + 2 * state.outset) * state.scale;

        // Reverted back to using standard POSITION format layout pipeline tracking
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        float quadWidth = width + state.xOffset * state.scale;
        float quadHeight = height + state.yOffset * state.scale;

        // Vertices mapped plain without UV offsets
        builder.addVertex(0F, 0F, 0F);
        builder.addVertex(0F, quadHeight, 0F);
        builder.addVertex(quadWidth, quadHeight, 0F);
        builder.addVertex(quadWidth, 0F, 0F);

        MeshData mesh = builder.buildOrThrow();

        GpuBufferSlice dynamicTransformsBuffer =
                RenderSystem.getDynamicUniforms().writeTransform(
                        RenderSystem.getModelViewMatrix(),
                        new Vector4f(),
                        new Vector3f(),
                        new Matrix4f()
                );

        GpuBufferSlice uniformBuffer = Uniform.STORAGE.writeUniform(buffer -> {
            Std140Builder.intoBuffer(buffer)
                         .putVec4(
                                 state.xOffset * state.scale,
                                 state.yOffset * state.scale,
                                 width,
                                 height
                         ) // packed into uniform hueBounds
                         .putVec4(state.colorCurrent); // packed into uniform currentColor
        });

        GpuBuffer vertexBuffer =
                PIPELINE_hue.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());

        RenderSystem.AutoStorageIndexBuffer indexStorage =
                RenderSystem.getSequentialBuffer(mesh.drawState().mode());

        GpuBuffer indexBuffer =
                indexStorage.getBuffer(mesh.drawState().indexCount());

        RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

        try (mesh;
             RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                     () -> "Hue Wheel Render Pass",
                     Objects.requireNonNullElse(RenderSystem.outputColorTextureOverride, renderTarget.getColorTextureView()),
                     OptionalInt.empty(),
                     renderTarget.useDepth ? Objects.requireNonNullElse(RenderSystem.outputDepthTextureOverride, renderTarget.getDepthTextureView()) : null,
                     OptionalDouble.empty()
             )) {

            pass.setPipeline(PIPELINE_hue);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", dynamicTransformsBuffer);
            pass.setUniform("Uniforms", uniformBuffer);
            pass.setVertexBuffer(0, vertexBuffer);
            pass.setIndexBuffer(indexBuffer, indexStorage.type());
            pass.drawIndexed(0, 0, mesh.drawState().indexCount(), 1);
        }

        prevState = state;
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return "Hue Wheel Render Pass";
    }

    public static class State implements PictureInPictureRenderState {
        final float outset;
        float x, y, width, height, scale, radius;
        Vector4f colorCurrent;

        float xOffset, yOffset;
        ScreenRectangle scissorArea, bounds;

        public State(GuiGraphics context, float x, float y, float radius, int currentColor) {
            this.scale = Minecraft.getInstance().getWindow().getGuiScale();
            this.radius = radius;

            this.width = radius * 2.0F;
            this.height = radius * 2.0F;

            this.x = x;
            this.y = y;
            this.xOffset = x - Mth.floor(x);
            this.yOffset = y - Mth.floor(y);

            this.colorCurrent = new Vector4f(
                    ARGB.red(currentColor) / 255F,
                    ARGB.green(currentColor) / 255F,
                    ARGB.blue(currentColor) / 255F,
                    ARGB.alpha(currentColor) / 255F
            );

            this.outset = 4.0F;

            this.scissorArea = context.scissorStack.peek();
            ScreenRectangle baseBounds = new ScreenRectangle(this.x0(), this.y0(), this.x1() - this.x0(), this.y1() - this.y0());
            this.bounds = scissorArea == null ? baseBounds : scissorArea.intersection(baseBounds);
        }

        @Override
        public int x0() {
            return Mth.floor(x) - (int) outset;
        }

        @Override
        public int x1() {
            return Mth.ceil(x + width + outset);
        }

        @Override
        public int y0() {
            return Mth.floor(y) - (int) outset;
        }

        @Override
        public int y1() {
            return Mth.ceil(y + height + outset);
        }

        @Override
        public float scale() {
            return 1F;
        }

        @Override
        public @Nullable ScreenRectangle scissorArea() {
            return scissorArea;
        }

        @Override
        public @Nullable ScreenRectangle bounds() {
            return bounds;
        }
    }

    public static class Uniform {
        private static final int SIZE = new Std140SizeCalculator()
                .putVec4() // hueBounds
                .putVec4() // currentColor
                .get();

        private static final DynamicUniformStorage<DynamicUniformStorage.@NotNull DynamicUniform> STORAGE =
                new DynamicUniformStorage<>("Hue UBO", SIZE, 4);

        public static void clear() {
            STORAGE.endFrame();
        }
    }
}