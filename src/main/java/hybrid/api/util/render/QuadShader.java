package hybrid.api.util.render;

import com.mojang.blaze3d.buffers.*;
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
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class QuadShader extends PictureInPictureRenderer<QuadShader.State> {

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

    private State lastState;

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
        return Objects.equals(lastState, state);
    }

    @Override
    protected void renderToTexture(State state, @NonNull PoseStack poseStack) {

        float width = (state.extentX + 2 * State.OUTSET) * state.scale;
        float height = (state.extentY + 2 * State.OUTSET) * state.scale;

        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        float textureWidth = width + state.subpixelX * state.scale;
        float textureHeight = height + state.subpixelY * state.scale;

        builder.addVertex(0F, 0F, 0F);
        builder.addVertex(0F, textureHeight, 0F);
        builder.addVertex(textureWidth, textureHeight, 0F);
        builder.addVertex(textureWidth, 0F, 0F);

        MeshData mesh = builder.buildOrThrow();

        GpuBufferSlice dynamicTransformsBuffer =
                RenderSystem.getDynamicUniforms().writeTransform(
                        RenderSystem.getModelViewMatrix(),
                        new Vector4f(),
                        new Vector3f(),
                        new Matrix4f()
                );

        GpuBufferSlice myUniformBuffer = Uniform.STORAGE.writeUniform(buffer -> {
            Std140Builder.intoBuffer(buffer)
                         .putVec4(
                                 state.subpixelX * state.scale + width * 0.5F,
                                 state.subpixelY * state.scale + height * 0.5F,
                                 state.extentX * state.scale,
                                 state.extentY * state.scale
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
                     () -> "hybrid-api Rounded Rectangle Render Pass",
                     Objects.requireNonNullElse(RenderSystem.outputColorTextureOverride, renderTarget.getColorTextureView()),
                     OptionalInt.empty(),
                     renderTarget.useDepth ? Objects.requireNonNullElse(RenderSystem.outputDepthTextureOverride, renderTarget.getDepthTextureView()) : null,
                     OptionalDouble.empty()
             )) {

            pass.setPipeline(PIPELINE_quad);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", dynamicTransformsBuffer);
            pass.setUniform("QuadUniforms", myUniformBuffer);
            pass.setVertexBuffer(0, vertexBuffer);
            pass.setIndexBuffer(indexBuffer, indexStorage.type());
            pass.drawIndexed(0, 0, mesh.drawState().indexCount(), 1);
        }

        lastState = state;
    }

    @Override
    protected @NonNull String getTextureLabel() {
        return "hybrid-api Rounded Rectangle PIP";
    }

    public static class State implements PictureInPictureRenderState {

        public final static float OUTSET = 14F;

        public final float left, top, right, bottom;
        public final float scale;
        public final Vector4f color;

        private final float subpixelX, subpixelY;
        private final float extentX, extentY;

        private final ScreenRectangle scissorArea;
        private final ScreenRectangle bounds;

        public float radiusRB, radiusRT, radiusLB, radiusLT;
        public float edgeSoftness = 1F;

        public State(GuiGraphics context, float x, float y, float width, float height, float radius, int color) {

            this.left = x;
            this.top = y;
            this.right = x + width;
            this.bottom = y + height;

            this.subpixelX = left - Mth.floor(left);
            this.subpixelY = top - Mth.floor(top);

            this.scale = Minecraft.getInstance().getWindow().getGuiScale();

            this.color = new Vector4f(
                    ARGB.red(color) / 255F,
                    ARGB.green(color) / 255F,
                    ARGB.blue(color) / 255F,
                    ARGB.alpha(color) / 255F
            );

            this.extentX = right - left;
            this.extentY = bottom - top;

            this.radiusLT = this.radiusRT = this.radiusLB = this.radiusRB =
                    Math.min(radius, Math.min(extentX, extentY) * 0.5F);

            this.scissorArea = context.scissorStack.peek();

            ScreenRectangle bounds = new ScreenRectangle(
                    this.x0(),
                    this.y0(),
                    this.x1() - this.x0(),
                    this.y1() - this.y0()
            );

            this.bounds = scissorArea == null ? bounds : scissorArea.intersection(bounds);
        }

        @Override
        public int x0() {
            return Mth.floor(left) - (int) OUTSET;
        }

        @Override
        public int x1() {
            return Mth.ceil(right + OUTSET);
        }

        @Override
        public int y0() {
            return Mth.floor(top) - (int) OUTSET;
        }

        @Override
        public int y1() {
            return Mth.ceil(bottom + OUTSET);
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

        private static final DynamicUniformStorage<DynamicUniformStorage.DynamicUniform> STORAGE =
                new DynamicUniformStorage<>("hybrid-api Rounded Rectangle UBO", SIZE, 4);

        public static void clear() {
            STORAGE.endFrame();
        }
    }
}