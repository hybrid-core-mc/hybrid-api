package hybrid.api.rendering;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import hybrid.api.theme.HybridTheme;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.HybridScreen;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static hybrid.api.HybridApi.mc;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL2.*;

public class HybridRenderer implements HybridRenderer2D {

    public static final HybridRenderer RENDERER_INSTANCE = new HybridRenderer();
    public static final List<ContextRenderTask> CONTEXT_LIST = new ArrayList<>();

    private static final NVGColor NVG_COLOR = NVGColor.create();
    private static final NVGColor GLOW_INNER = NVGColor.create();
    private static final NVGColor GLOW_OUTER = NVGColor.create();
    private static final NVGPaint GLOW_PAINT = NVGPaint.create();

    private static ColorPickerRenderer colorPicker;
    private static GuiFramebuffer GUI_FBO;
    private static GpuTextureView GUI_VIEW;

    private static long CONTEXT = -1L;

    public static void init() {
        CONTEXT = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        if (CONTEXT <= 0) throw new RuntimeException("couldnt init nvg context");
        nvgGlobalCompositeOperation(CONTEXT, NVG_SOURCE_OVER);
        colorPicker = new ColorPickerRenderer(CONTEXT);
    }

    public static void render(DrawContext context, RenderTickCounter tickDelta) {

        if (!(mc.currentScreen instanceof HybridScreen)) return;

        if (GUI_FBO == null) {
            GUI_FBO = new GuiFramebuffer();
        }

        int fbW = mc.getWindow().getFramebufferWidth();
        int fbH = mc.getWindow().getFramebufferHeight();

        if (GUI_FBO.resizeIfNeeded(fbW, fbH)) {

            if (GUI_VIEW != null) {
                GUI_VIEW.close();
            }

            GUI_VIEW = RenderSystem.getDevice().createTextureView(GUI_FBO.getColorAttachment());
        }

        setup();

        Framebuffer main = mc.getFramebuffer();
        Framebuffer guiFbo = GUI_FBO;

        guiFbo.copyDepthFrom(main);

        GpuTexture guiColor = guiFbo.getColorAttachment();
        if (guiColor == null) return;

        GlBackend backend = (GlBackend) RenderSystem.getDevice();

        int mainFboId = ((GlTexture) main.getColorAttachment())
                .getOrCreateFramebuffer(backend.getBufferManager(), main.getDepthAttachment());

        int guiFboId = ((GlTexture) guiColor)
                .getOrCreateFramebuffer(backend.getBufferManager(), main.getDepthAttachment());


        GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, mainFboId);
        GlStateManager._glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, guiFboId);

        GL30.glBlitFramebuffer(
                0, 0, fbW, fbH,
                0, 0, fbW, fbH,
                GL11.GL_COLOR_BUFFER_BIT,
                GL11.GL_NEAREST
        );


        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, guiFboId);
        GlStateManager._viewport(0, 0, fbW, fbH);

        float pixelRatio = (float) fbW / mc.getWindow().getWidth();

        nvgBeginFrame(
                CONTEXT,
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(),
                pixelRatio
        );

        HybridRenderQueue.clear();
        HybridRenderQueue.renderAll(RENDERER_INSTANCE);

        nvgEndFrame(CONTEXT);


        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainFboId);
        GlStateManager._viewport(0, 0, fbW, fbH);

        if (GUI_VIEW == null) {
            GUI_VIEW = RenderSystem.getDevice().createTextureView(GUI_FBO.getColorAttachment());
        }

        context.state.addSimpleElement(
                new TexturedQuadGuiElementRenderState(
                        RenderPipelines.GUI_TEXTURED,
                        TextureSetup.of(
                                GUI_VIEW,
                                RenderSystem.getSamplerCache().get(FilterMode.NEAREST)
                        ),
                        new Matrix3x2f(context.getMatrices()),
                        0,
                        0,
                        mc.getWindow().getScaledWidth(),
                        mc.getWindow().getScaledHeight(),
                        0.0f,
                        1.0f,
                        1.0f,
                        0.0f,
                        -1,
                        context.scissorStack.peekLast()
                )
        );

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
        NVG_COLOR.r(color.getRed() / 255f).g(color.getGreen() / 255f).b(color.getBlue() / 255f).a(color.getAlpha() / 255f);
        nvgFillColor(ctx, NVG_COLOR);
    }

    private static void setStrokeColor(long ctx, Color color) {
        NVG_COLOR.r(color.getRed() / 255f).g(color.getGreen() / 255f).b(color.getBlue() / 255f).a(color.getAlpha() / 255f);
        nvgStrokeColor(ctx, NVG_COLOR);
    }

    @Override
    public void drawColorTriangle(ScreenBounds bounds, float hue, float padding) {
        if (colorPicker == null) return;
        colorPicker.drawColorPicker(bounds, hue, padding);
    }

    @Override
    public void drawAlphaSlider(ScreenBounds bounds, Color color) {

        int checker = 6;

        nvgSave(CONTEXT);

        NVGColor c1 = NVGColor.calloc();
        NVGColor c2 = NVGColor.calloc();

        nvgRGBA((byte) 180, (byte) 180, (byte) 180, (byte) 255, c1);
        nvgRGBA((byte) 120, (byte) 120, (byte) 120, (byte) 255, c2);

        int bx = bounds.getX();
        int by = bounds.getY();
        int bw = bounds.getWidth();
        int bh = bounds.getHeight();

        int right1 = bx + bw;
        int bottom = by + bh;

        for (int x = bx; x < right1; x += checker) {
            for (int y = by; y < bottom; y += checker) {

                int w = Math.min(checker, right1 - x);
                int h = Math.min(checker, bottom - y);

                boolean even = ((x / checker) + (y / checker)) % 2 == 0;

                nvgBeginPath(CONTEXT);
                nvgRect(CONTEXT, x, y, w, h);
                nvgFillColor(CONTEXT, even ? c1 : c2);
                nvgFill(CONTEXT);
            }
        }

        NVGColor left = NVGColor.calloc();
        NVGColor right = NVGColor.calloc();

        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 0, left);
        nvgRGBA((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) 255, right);

        NVGPaint paint = NVGPaint.calloc();
        nvgLinearGradient(CONTEXT, bounds.getX(), bounds.getY(), bounds.getX() + bounds.getWidth(), bounds.getY(), left, right, paint);

        nvgBeginPath(CONTEXT);
        nvgRect(CONTEXT, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        nvgFillPaint(CONTEXT, paint);
        nvgFill(CONTEXT);

        NVGColor outline = NVGColor.calloc();
        nvgRGBA((byte) 0, (byte) 0, (byte) 0, (byte) 180, outline);

        nvgBeginPath(CONTEXT);
        nvgRect(CONTEXT, bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
        nvgStrokeColor(CONTEXT, outline);
        nvgStrokeWidth(CONTEXT, 1f);
        nvgStroke(CONTEXT);

        float radius = 6f;
        float innerStroke = 1.2f;
        float inset = innerStroke / 2f;

        NVGColor innerOrange = NVGColor.calloc();
        Color innerBorder = HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor);

        nvgRGBA(
                (byte) innerBorder.getRed(),
                (byte) innerBorder.getGreen(),
                (byte) innerBorder.getBlue(),
                (byte) innerBorder.getAlpha(),
                innerOrange
        );

        float outerStroke = 5f;
        float expand = outerStroke / 5f;

        NVGColor outerBlack = NVGColor.calloc();
        Color c = HybridThemeMap.get(ThemeColorKey.modsBackgroundColor);

        nvgRGBA((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue(), (byte) c.getAlpha(), outerBlack);

        nvgBeginPath(CONTEXT);
        nvgRoundedRect(CONTEXT, bounds.getX() - expand, bounds.getY() - expand, bounds.getWidth() + expand * 2, bounds.getHeight() + expand * 2, radius + expand);
        nvgStrokeColor(CONTEXT, outerBlack);
        nvgStrokeWidth(CONTEXT, outerStroke);
        nvgStroke(CONTEXT);

        nvgBeginPath(CONTEXT);
        nvgRoundedRect(
                CONTEXT,
                bounds.getX() + inset,
                bounds.getY() + inset,
                bounds.getWidth() - inset * 2,
                bounds.getHeight() - inset * 2,
                radius
        );
        nvgStrokeColor(CONTEXT, innerOrange);
        nvgStrokeWidth(CONTEXT, innerStroke);
        nvgStroke(CONTEXT);

        innerOrange.free();
        outerBlack.free();
        paint.free();
        left.free();
        right.free();
        c1.free();
        c2.free();
        outline.free();

        nvgRestore(CONTEXT);
    }

    @Override
    public void drawQuad(ScreenBounds bounds, Color color, int radius) {
        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, color);
        nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, radius);
        nvgFill(CONTEXT);
    }

    @Override
    public void drawQuad(ScreenBounds bounds, Color color, int topRight, int topLeft, int bottomRight, int bottomLeft) {

        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, color);

        nvgRoundedRectVarying(
                CONTEXT,
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height,
                topRight, topLeft, bottomRight, bottomLeft
        );

        nvgFill(CONTEXT);
    }

    @Override
    public void drawQuad(ScreenBounds bounds, Color color) {
        drawQuad(bounds, color, HybridTheme.cornerRadius);
    }

    @Override
    public void beginScissors(ScreenBounds bounds) {
        nvgSave(CONTEXT);
        nvgScissor(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    @Override
    public void endScissors() {
        nvgRestore(CONTEXT);
    }

    @Override
    public void drawOutlineQuad(ScreenBounds bounds, Color fill, Color outline, int radius, int outlineRadius) {

        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, fill);
        nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, radius);
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
    public void drawLine(ScreenBounds bounds, Color color, float thickness) {
        drawLine(bounds, color, thickness, 0f);
    }

    public void drawLine(ScreenBounds bounds, Color color, float thickness, float yOffsetPx) {

        float x1 = bounds.getX();
        float y1 = bounds.getY() + yOffsetPx;

        float x2 = bounds.getWidth();
        float y2 = bounds.getHeight() + yOffsetPx;

        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, x1, y1);
        nvgLineTo(CONTEXT, x2, y2);

        NVG_COLOR
                .r(color.getRed() / 255f)
                .g(color.getGreen() / 255f)
                .b(color.getBlue() / 255f)
                .a(color.getAlpha() / 255f);

        nvgStrokeColor(CONTEXT, NVG_COLOR);
        nvgStrokeWidth(CONTEXT, thickness);

        nvgLineCap(CONTEXT, NVG_BUTT);

        nvgStroke(CONTEXT);
    }
}