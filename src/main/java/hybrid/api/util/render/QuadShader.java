package hybrid.api.util.render;

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

public class QuadShader extends PictureInPictureRenderer<QuadShader.@NotNull State> {

    RenderPipeline PIPELINE_quad = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                          .withLocation(Identifier.fromNamespaceAndPath("hybrid-api", "pipeline/quad"))
                          .withFragmentShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/quad"))
                          .withVertexShader(Identifier.fromNamespaceAndPath("hybrid-api", "core/quad"))
                          .withBlend(BlendFunction.TRANSLUCENT)
                          .withUniform("QuadUniforms", UniformType.UNIFORM_BUFFER)
                          .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
                          .build()
    );

    private State prevState;

    protected QuadShader(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    public static void init() {
        SpecialGuiElementRegistry.register(context -> new QuadShader(context.vertexConsumers()));
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

        float quadWidth = width + state.subPixelOffsetX * state.scale;
        float quadHeight = height + state.subPixelOffsetY * state.scale;

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
                                 state.subPixelOffsetX * state.scale + width * 0.5F,
                                 state.subPixelOffsetY * state.scale + height * 0.5F,
                                 state.width * state.scale,
                                 state.height * state.scale
                         )
                         .putVec4(
                                 state.radiusRB * state.scale,
                                 state.radiusRT * state.scale,
                                 state.radiusLB * state.scale,
                                 state.radiusLT * state.scale
                         )
                         .putVec4(state.color)
                         .putFloat(state.edgeSoftness * state.scale);
        });

        GpuBuffer vertexBuffer =
                PIPELINE_quad.getVertexFormat().uploadImmediateVertexBuffer(mesh.vertexBuffer());

        RenderSystem.AutoStorageIndexBuffer indexStorage =
                RenderSystem.getSequentialBuffer(mesh.drawState().mode());

        GpuBuffer indexBuffer =
                indexStorage.getBuffer(mesh.drawState().indexCount());

        RenderTarget renderTarget = Minecraft.getInstance().getMainRenderTarget();

        try (mesh;
             RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                     () -> "Quad Render Pass",
                     Objects.requireNonNullElse(RenderSystem.outputColorTextureOverride, renderTarget.getColorTextureView()),
                     OptionalInt.empty(),
                     renderTarget.useDepth ? Objects.requireNonNullElse(RenderSystem.outputDepthTextureOverride, renderTarget.getDepthTextureView()) : null,
                     OptionalDouble.empty()
             )) {

            pass.setPipeline(PIPELINE_quad);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", dynamicTransformsBuffer);
            pass.setUniform("QuadUniforms", uniformBuffer);
            pass.setVertexBuffer(0, vertexBuffer);
            pass.setIndexBuffer(indexBuffer, indexStorage.type());
            pass.drawIndexed(0, 0, mesh.drawState().indexCount(), 1);
        }

        prevState = state;
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return "Quad Render Pass";
    }

    public static class State implements PictureInPictureRenderState {

        public final float x, y;
        public final float width, height;
        public final float scale;
        public final Vector4f color;

        public final float subPixelOffsetX, subPixelOffsetY;

        public final ScreenRectangle scissorArea;
        public final ScreenRectangle bounds;

        public float radiusRB, radiusRT, radiusLB, radiusLT;
        public float edgeSoftness = 1F;

        public final float outset;

        public State(GuiGraphics context, float x, float y, float width, float height, float radius, int color) {

            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.subPixelOffsetX = x - Mth.floor(x);
            this.subPixelOffsetY = y - Mth.floor(y);

            this.scale = Minecraft.getInstance().getWindow().getGuiScale();

            this.color = new Vector4f(
                    ARGB.red(color) / 255F,
                    ARGB.green(color) / 255F,
                    ARGB.blue(color) / 255F,
                    ARGB.alpha(color) / 255F
            );

            radius = Math.min(radius, Math.min(width, height) * 0.5F);

            this.radiusLT = this.radiusRT = this.radiusLB = this.radiusRB = radius;

            float maxRadius = radius;

            this.outset = Math.max(maxRadius, edgeSoftness * 2.5F);

            this.scissorArea = context.scissorStack.peek();

            ScreenRectangle baseBounds = new ScreenRectangle(
                    this.x0(),
                    this.y0(),
                    this.x1() - this.x0(),
                    this.y1() - this.y0()
            );

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
                .putVec4()
                .putFloat()
                .get();

        private static final DynamicUniformStorage<DynamicUniformStorage.@NotNull DynamicUniform> STORAGE =
                new DynamicUniformStorage<>("Quad UBO", SIZE, 4);

        public static void clear() {
            STORAGE.endFrame();
        }
    }
}