package hybrid.api.shader;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import hybrid.api.HybridApi;
import hybrid.api.rendering.ScreenBounds;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
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
    public static final Int2ObjectArrayMap<HueShader> RESTORE = new Int2ObjectArrayMap<>(8);

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
    private final Color color;
    private final TextureSetup textureSetup;
    private final ScreenBounds bounds;
    private final @Nullable ScreenRect scissorArea;

    public GpuBufferSlice uniformBuffer;

    public HueShader(DrawContext context, ScreenBounds bounds, Color color) {
        this.bounds = bounds;
        this.color = color;
        this.scissorArea = context.scissorStack.peekLast();
        this.textureSetup = TextureSetup.of(RenderSystem.outputColorTextureOverride, RenderSystem.getSamplerCache().get(FilterMode.NEAREST));
        RESTORE.put(System.identityHashCode(this.textureSetup), this);
    }

    public static void drawHueRing(DrawContext context, ScreenBounds screenBounds, Color color) {
        context.state.addSimpleElement(new HueShader(context, screenBounds, color));
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

        this.uniformBuffer = Uniforms.instance.write(new Uniforms.Value(
                bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), color
        ));
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
        return scissorArea;
    }


    public @Nullable ScreenRect bounds() {
        ScreenRect scissor = scissorArea();
        ScreenRect rect = new ScreenRect(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        return scissor == null ? rect : scissor.intersection(rect);
    }

    public static class Uniforms {
        public static final int SIZE = new Std140SizeCalculator()
                .putVec4()
                .putVec4()
                .get()
                ;

        public static final Uniforms instance = new Uniforms();
        private final DynamicUniformStorage<Value> storage =
                new DynamicUniformStorage<>("HUE UBO", SIZE, 4);

        public GpuBufferSlice write(Value value) {
            return storage.write(value);
        }

        public void clear() {
            storage.clear();
        }

        public record Value(
                float x, float y, float width, float height, Color color
        ) implements DynamicUniformStorage.Uploadable {
            @Override
            public void write(java.nio.ByteBuffer buffer) {
                Std140Builder.intoBuffer(buffer).putVec4(x, y, width, height)
                             .putVec4((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, 1.0f)
                ;
            }
        }
    }
}