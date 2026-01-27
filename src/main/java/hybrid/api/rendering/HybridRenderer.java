package hybrid.api.rendering;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import hybrid.api.ui.Theme;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import java.awt.*;

import static hybrid.api.HybridApi.mc;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL2.*;

public class HybridRenderer implements HybridRenderer2D {
    public static final HybridRenderer RENDERER_INSTANCE = new HybridRenderer();
    private static final NVGColor NVG_COLOR = NVGColor.create();
    private static final NVGColor GLOW_INNER = NVGColor.create();
    private static final NVGColor GLOW_OUTER = NVGColor.create();
    private static final NVGPaint GLOW_PAINT = NVGPaint.create();

    private static long CONTEXT = -1L;

    public static void init() {
        CONTEXT = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES | NVG_DEBUG);
        if (CONTEXT == 0 || CONTEXT == -1L)
            throw new RuntimeException("couldnt init nvg context");

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

        HybridRenderQueue.renderAll(RENDERER_INSTANCE);

        nvgEndFrame(CONTEXT);
        restore();

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

    @Override
    public void drawQuad(ScreenBounds bounds, Color color, int radius) {
        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, color);
        nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, radius);
        nvgFill(CONTEXT);
    }

    @Override
    public void drawOutlineQuad(ScreenBounds bounds, Color color, Color outline, int radius, int outlineRadius) {
        nvgSave(CONTEXT);

        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, color);
        nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, radius);
        nvgFill(CONTEXT);

        int glowSteps = 5;

        for (int i = 0; i < glowSteps; i++) {
            float alpha = (1.0f - (i / (float) glowSteps)) * 0.25f;
            setColor(CONTEXT, new Color(outline.getRed(), outline.getGreen(), outline.getBlue(), (int)(alpha * 255)));

            nvgBeginPath(CONTEXT);
            nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, radius);
            nvgStrokeWidth(CONTEXT, (float) outlineRadius + i * 2);
            nvgStroke(CONTEXT);
        }

        setColor(CONTEXT, outline);
        nvgBeginPath(CONTEXT);
        nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, radius);
        nvgStrokeWidth(CONTEXT, outlineRadius);
        nvgStroke(CONTEXT);

        nvgRestore(CONTEXT);
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

}
