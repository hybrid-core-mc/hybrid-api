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

public class CircleShader extends PictureInPictureRenderer<CircleShader.@NotNull State> {

    RenderPipeline PIPELINE_circle = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                          .withLocation(Identifier.fromNamespaceAndPath("hybrid-api", "pipeline/circle"))
                          .withFragmentShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/circle"))
                          .withVertexShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/position"))
                          .withBlend(BlendFunction.TRANSLUCENT)
                          .withUniform("CircleUniforms", UniformType.UNIFORM_BUFFER)
                          .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
                          .build()
    );

    private State prevState;

    protected CircleShader(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    public static void init() {
        SpecialGuiElementRegistry.register(context -> new CircleShader(context.vertexConsumers()));
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

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        float quadWidth = width + state.xOffset * state.scale;
        float quadHeight = height + state.yOffset * state.scale;

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
                                 state.xOffset * state.scale + width * 0.5F,
                                 state.yOffset * state.scale + height * 0.5F,
                                 state.radius * state.scale,
                                 0.0F
                         )
                         .putVec4(state.color)
                         .putFloat(state.edgeSoftness * state.scale)
                         .putFloat(state.outset * state.scale)
                         .putFloat(state.glowing ? 1.0F : 0.0F)
            ;
        });

        GpuBuffer vertexBuffer =
                PIPELINE_circle.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());

        RenderSystem.AutoStorageIndexBuffer indexStorage =
                RenderSystem.getSequentialBuffer(mesh.drawState().mode());

        GpuBuffer indexBuffer =
                indexStorage.getBuffer(mesh.drawState().indexCount());

        RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

        try (mesh;
             RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                     () -> "Circle Render Pass",
                     Objects.requireNonNullElse(RenderSystem.outputColorTextureOverride, renderTarget.getColorTextureView()),
                     OptionalInt.empty(),
                     renderTarget.useDepth ? Objects.requireNonNullElse(RenderSystem.outputDepthTextureOverride, renderTarget.getDepthTextureView()) : null,
                     OptionalDouble.empty()
             )) {

            pass.setPipeline(PIPELINE_circle);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", dynamicTransformsBuffer);
            pass.setUniform("CircleUniforms", uniformBuffer);
            pass.setVertexBuffer(0, vertexBuffer);
            pass.setIndexBuffer(indexBuffer, indexStorage.type());
            pass.drawIndexed(0, 0, mesh.drawState().indexCount(), 1);
        }

        prevState = state;
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return "Circle Render Pass";
    }

    public static class State implements PictureInPictureRenderState {

        final float outset;
        float edgeSoftness = 1.0F;
        float x, y, width, height, scale, radius;
        Vector4f color;
        boolean glowing;

        float xOffset, yOffset;
        ScreenRectangle scissorArea, bounds;

        public State(GuiGraphics context, float x, float y, float width, float height, float radius, int color, boolean glowing) {
            this.scale = Minecraft.getInstance().getWindow().getGuiScale();

            this.radius = radius;
            this.glowing = glowing;

            this.width = radius * 2.0F;
            this.height = radius * 2.0F;

            this.x = x;
            this.y = y;
            this.xOffset = x - Mth.floor(x);
            this.yOffset = y - Mth.floor(y);

            this.color = new Vector4f(
                    ARGB.red(color) / 255F,
                    ARGB.green(color) / 255F,
                    ARGB.blue(color) / 255F,
                    ARGB.alpha(color) / 255F
            );


            this.outset = this.glowing ? (this.radius * 2.5F) : (this.edgeSoftness * 2.5F);

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
                .putVec4()
                .putVec4()
                .putFloat()
                .putFloat()
                .putFloat()
                .get()
                ;

        private static final DynamicUniformStorage<DynamicUniformStorage.@NotNull DynamicUniform> STORAGE =
                new DynamicUniformStorage<>("Circle UBO", SIZE, 4);

        public static void clear() {
            STORAGE.endFrame();
        }
    }
}