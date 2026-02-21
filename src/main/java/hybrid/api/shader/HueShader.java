package hybrid.api.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import hybrid.api.HybridApi;
import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;

public class HueShader implements SimpleGuiElementRenderState {

    public static final RenderPipeline HUE_PIPELINE =
            RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
                          .withLocation(Identifier.of(HybridApi.MOD_ID, "pipeline/hue"))
                          .withFragmentShader(Identifier.of(HybridApi.MOD_ID, "core/hue"))
                          .withVertexShader(Identifier.of(HybridApi.MOD_ID, "core/position"))
                          .withDepthWrite(true)
                          .withCull(false)
                          .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                          .withBlend(BlendFunction.TRANSLUCENT)
                          .withUniform("Uniforms", UniformType.UNIFORM_BUFFER)
                          .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.QUADS)
                          .build()
            ;

    public static final ThreadLocal<GpuBufferSlice> CURRENT_UNIFORM = new ThreadLocal<>();

    private static final int UNIFORM_SIZE = new Std140SizeCalculator().putVec4().putFloat().get();

    private static final DynamicUniformStorage<HueUniforms> UNIFORMS = new DynamicUniformStorage<>("Hue UBO", UNIFORM_SIZE, 4);

    private final DrawContext context;
    private final ScreenBounds bounds;
    private final Color color;
    private final TextureSetup textureSetup = TextureSetup.empty();

    public HueShader(DrawContext context, ScreenBounds bounds,Color color) {
        this.context = context;
        this.bounds = bounds;
        this.color = color;
    }

    public static void drawHueRing(DrawContext context, ScreenBounds bounds,Color color) {
        context.state.addSimpleElement(new HueShader(context, bounds,color));
    }

    public static void clearUniforms() {
        UNIFORMS.clear();
        CURRENT_UNIFORM.remove();
    }

    @Override
    public void setupVertices(VertexConsumer vertices) {
        float x1 = bounds.getX();
        float y1 = bounds.getY();
        float x2 = x1 + bounds.getWidth();
        float y2 = y1 + bounds.getHeight();

        vertices.vertex(x1, y1, 0);
        vertices.vertex(x1, y2, 0);
        vertices.vertex(x2, y2, 0);
        vertices.vertex(x2, y1, 0);

        GpuBufferSlice slice = UNIFORMS.write(new HueUniforms(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(),color));

        CURRENT_UNIFORM.set(slice);
    }

    @Override
    public @NotNull RenderPipeline pipeline() {
        return HUE_PIPELINE;
    }

    @Override
    public @NotNull TextureSetup textureSetup() {
        return textureSetup;
    }

    @Override
    public @Nullable ScreenRect scissorArea() {
        return context.scissorStack.peekLast();
    }

    @Override
    public @Nullable ScreenRect bounds() {
        ScreenRect scissor = scissorArea();
        ScreenRect rect = new ScreenRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        return scissor == null ? rect : Objects.requireNonNull(scissor).intersection(rect);
    }

    public record HueUniforms(float x, float y, float width, float height, Color color) implements DynamicUniformStorage.Uploadable {

        @Override
        public void write(java.nio.ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer).putVec4(x, y, width, height).putVec4((float) color.getRed() /255, (float) color.getGreen() /255, (float) color.getBlue() /255,1.0f);
        }
    }
}