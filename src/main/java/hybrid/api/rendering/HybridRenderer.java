package hybrid.api.rendering;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import hybrid.api.theme.Theme;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static hybrid.api.HybridApi.mc;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL2.*;

public class HybridRenderer implements HybridRenderer2D {
    public static final HybridRenderer RENDERER_INSTANCE = new HybridRenderer();
    public static final List<Consumer<DrawContext>> CONTEXT_LIST = new ArrayList<>();
    private static final NVGColor NVG_COLOR = NVGColor.create();
    private static final NVGColor GLOW_INNER = NVGColor.create();
    private static final NVGColor GLOW_OUTER = NVGColor.create();
    private static final NVGPaint GLOW_PAINT = NVGPaint.create();

    private static long CONTEXT = -1L;

    public static void init() {
        CONTEXT = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES | NVG_DEBUG);
        if (CONTEXT == 0 || CONTEXT == -1L)
            throw new RuntimeException("couldnt init nvg context");

//        Runtime
//                .getRuntime()
//                .addShutdownHook(new Thread(() -> {
//                    RenderSystem.assertOnRenderThread();
//                    mc.execute(HybridRenderer::onShutdown);
//                }));
//

        nvgGlobalCompositeOperation(CONTEXT, NVG_SOURCE_OVER);
    }


    public static void render() {

        Framebuffer frameBuffer = mc.getFramebuffer();

        int frameBufferWidth = mc.getWindow().getFramebufferWidth();
        int frameBufferHeight = mc.getWindow().getFramebufferHeight();

        setup();

        GpuTexture color = frameBuffer.getColorAttachment();
        GpuTexture depth = frameBuffer.getDepthAttachment();

        assert color != null;


        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, ((GlTexture) color).getOrCreateFramebuffer(((GlBackend) RenderSystem.getDevice()).getBufferManager(), depth));

        GlStateManager._viewport(0, 0, frameBufferWidth, frameBufferHeight);

        nvgBeginFrame(CONTEXT,
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(),
                1.0f
        );

        float scale = (float) frameBufferWidth / mc.getWindow().getWidth();
        nvgScale(CONTEXT, scale, scale);

        HybridRenderQueue.clear();

        HybridRenderQueue.renderAll(RENDERER_INSTANCE);

        nvgEndFrame(CONTEXT);
        restore();


    }

    private static void setup() {
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager._disableDepthTest();
        GlStateManager._disableCull();
    }

    private static void restore() {
        GlStateManager._enableDepthTest();
        GlStateManager._enableCull();
        GlStateManager._disableBlend();
    }

    private static void setColor(long ctx, Color color) {
        NVG_COLOR.r(color.getRed() / 255f)
                .g(color.getGreen() / 255f)
                .b(color.getBlue() / 255f)
                .a(color.getAlpha() / 255f);

        nvgFillColor(ctx, NVG_COLOR);
    }
    private static void setStrokeColor(long ctx, Color color) {
        NVG_COLOR.r(color.getRed() / 255f)
                .g(color.getGreen() / 255f)
                .b(color.getBlue() / 255f)
                .a(color.getAlpha() / 255f);

        nvgStrokeColor(ctx, NVG_COLOR);
    }

    @Override
    public void drawQuad(ScreenBounds bounds, Color color, int radius) {
        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, color);
        nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, radius);
        nvgFill(CONTEXT);
    }
    @Override
    public void beginScissors(ScreenBounds bounds) {
        nvgSave(CONTEXT);
        nvgScissor(
                CONTEXT,
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height
        );
    }

    public void endScissors() {
        nvgRestore(CONTEXT);
    }

    @Override
    public void drawOutlineQuad(ScreenBounds bounds, Color fill, Color outline, int radius, int outlineRadius) {

        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, fill);
        nvgRoundedRect(
                CONTEXT,
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height,
                radius
        );
        nvgFill(CONTEXT);

        float half = outlineRadius / 2f;

        nvgBeginPath(CONTEXT);
        setStrokeColor(CONTEXT, outline);

        nvgRoundedRect(
                CONTEXT,
                bounds.x + half,
                bounds.y + half,
                bounds.width - outlineRadius,
                bounds.height - outlineRadius,
                radius
        );

        nvgStrokeWidth(CONTEXT, outlineRadius);
        nvgStroke(CONTEXT);
    }


    @Override
    public void drawQuad(ScreenBounds bounds, Color color) {
        drawQuad(bounds, color, Theme.cornerRadius);
    }

    @Override
    public void drawCircle(ScreenBounds bounds, Color color) {
        float cx = bounds.x + bounds.width / 2f;
        float cy = bounds.y + bounds.height / 2f;
        float radius = Math.min(bounds.width, bounds.height) / 2f;

        float glowSize = 2f;
        float glowRadius = radius + glowSize;

        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 255, GLOW_INNER);

        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 0, GLOW_OUTER);

        nvgRadialGradient(CONTEXT, cx, cy, radius, glowRadius, GLOW_INNER, GLOW_OUTER, GLOW_PAINT);

        nvgBeginPath(CONTEXT);
        nvgCircle(CONTEXT, cx, cy, glowRadius);
        nvgFillPaint(CONTEXT, GLOW_PAINT);
        nvgFill(CONTEXT);
    }

    @Override
    public void drawHorizontalLine(ScreenBounds bounds, Color color, float distance) {

        float y = bounds.y + bounds.height / 2f;

        float halfWidth = bounds.width / 2f;
        float solidHalf = halfWidth * distance;

        float leftFadeStart = bounds.x + halfWidth - solidHalf;
        float rightFadeStart = bounds.x + halfWidth + solidHalf;

        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 0, GLOW_OUTER);

        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 255, GLOW_INNER);

        nvgLinearGradient(CONTEXT, bounds.x, y, leftFadeStart, y, GLOW_OUTER, GLOW_INNER, GLOW_PAINT);

        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, bounds.x, y);
        nvgLineTo(CONTEXT, leftFadeStart, y);
        nvgStrokePaint(CONTEXT, GLOW_PAINT);
        nvgStrokeWidth(CONTEXT, bounds.height);
        nvgStroke(CONTEXT);

        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, leftFadeStart, y);
        nvgLineTo(CONTEXT, rightFadeStart, y);
        nvgStrokeColor(CONTEXT, GLOW_INNER);
        nvgStrokeWidth(CONTEXT, bounds.height);
        nvgStroke(CONTEXT);

        nvgLinearGradient(CONTEXT, rightFadeStart, y, bounds.x + bounds.width, y, GLOW_INNER, GLOW_OUTER, GLOW_PAINT);

        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, rightFadeStart, y);
        nvgLineTo(CONTEXT, bounds.x + bounds.width, y);
        nvgStrokePaint(CONTEXT, GLOW_PAINT);
        nvgStrokeWidth(CONTEXT, bounds.height);
        nvgStroke(CONTEXT);
    }

    @Override
    public void drawVerticalLine(ScreenBounds bounds, Color color, float distance) {

        float x = bounds.x + bounds.width / 2f;

        float halfHeight = bounds.height / 2f;
        float solidHalf = halfHeight * distance;

        float topFadeStart = bounds.y + halfHeight - solidHalf;
        float bottomFadeStart = bounds.y + halfHeight + solidHalf;

        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 0, GLOW_OUTER);

        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 255, GLOW_INNER);

        nvgLinearGradient(CONTEXT, x, bounds.y, x, topFadeStart, GLOW_OUTER, GLOW_INNER, GLOW_PAINT);

        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, x, bounds.y);
        nvgLineTo(CONTEXT, x, topFadeStart);
        nvgStrokePaint(CONTEXT, GLOW_PAINT);
        nvgStrokeWidth(CONTEXT, bounds.width);
        nvgStroke(CONTEXT);

        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, x, topFadeStart);
        nvgLineTo(CONTEXT, x, bottomFadeStart);
        nvgStrokeColor(CONTEXT, GLOW_INNER);
        nvgStrokeWidth(CONTEXT, bounds.width);
        nvgStroke(CONTEXT);

        nvgLinearGradient(CONTEXT, x, bottomFadeStart, x, bounds.y + bounds.height, GLOW_INNER, GLOW_OUTER, GLOW_PAINT);

        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, x, bottomFadeStart);
        nvgLineTo(CONTEXT, x, bounds.y + bounds.height);
        nvgStrokePaint(CONTEXT, GLOW_PAINT);
        nvgStrokeWidth(CONTEXT, bounds.width);
        nvgStroke(CONTEXT);
    }


}
