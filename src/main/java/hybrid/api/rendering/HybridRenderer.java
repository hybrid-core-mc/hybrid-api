package hybrid.api.rendering;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import hybrid.api.HybridApi;
import hybrid.api.theme.HybridTheme;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33C;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static hybrid.api.HybridApi.mc;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;

public class HybridRenderer implements HybridRenderer2D {

    public static final HybridRenderer RENDERER_INSTANCE = new HybridRenderer();
    public static final List<ContextRenderTask> CONTEXT_LIST = new ArrayList<>();

    private static final NVGColor NVG_COLOR = NVGColor.create();
    private static final NVGColor GLOW_INNER = NVGColor.create();
    private static final NVGColor GLOW_OUTER = NVGColor.create();
    private static final NVGPaint GLOW_PAINT = NVGPaint.create();

    private static ColorPickerRenderer colorPicker;
    private static GpuTextureView GUI_VIEW;

    private static long CONTEXT = -1L;
    private static int font;
    private static ByteBuffer FONT_BUFFER;

    public static void init() {
        CONTEXT = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);

        if (CONTEXT <= 0) throw new RuntimeException("Failed to create NanoVG context");

        System.out.println("[NanoVG] Context created: " + CONTEXT);

        try {
            FONT_BUFFER = loadResource("/assets/hybrid-api/font/inter-regular.ttf");
            System.out.println("[NanoVG] Font buffer loaded: " + (FONT_BUFFER != null));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font resource", e);
        }

        font = nvgCreateFontMem(CONTEXT, "inter", FONT_BUFFER, false);

        System.out.println("[NanoVG] Font ID: " + font);

        if (font == -1) {
            throw new RuntimeException("NanoVG font failed to load (ID = -1)");
        }
    }

    public static ByteBuffer loadResource(String path) throws IOException {
        try (InputStream source = HybridApi.class.getResourceAsStream(path)) {

            if (source == null) {
                throw new FileNotFoundException("Resource not found: " + path);
            }

            byte[] bytes = source.readAllBytes();

            ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            return buffer;
        }
    }



    public static void render() {
        int fbW = mc.getWindow().getFramebufferWidth();
        int fbH = mc.getWindow().getFramebufferHeight();

        setup();

        Framebuffer main = mc.getFramebuffer();
        GlBackend backend = (GlBackend) RenderSystem.getDevice();

        int mainFboId = ((GlTexture) main.getColorAttachment()).getOrCreateFramebuffer(backend.getBufferManager(), main.getDepthAttachment());

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, mainFboId);
        GlStateManager._viewport(0, 0, fbW, fbH);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL33C.glBindSampler(0, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        float pixelRatio = (float) fbW / mc.getWindow().getWidth();

        nvgBeginFrame(CONTEXT,
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(),
                pixelRatio
        );
        rendertext();

        nvgEndFrame(CONTEXT);

        restore();
    }

    public static void rendertext(){
        // DEBUG TEXT
        nvgFontFace(CONTEXT, "inter");
        nvgFontSize(CONTEXT, 100f);;
        nvgFontBlur(CONTEXT, 0f);
        nvgTextAlign(CONTEXT, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

        setColor(CONTEXT, Color.RED);

        float result = nvgText(CONTEXT, 10, 10, "hello world!");
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
        beginScissors(bounds, false);
    }

    @Override
    public void endScissors() {
        nvgRestore(CONTEXT);
    }

    @Override
    public void drawColorTriangle(ScreenBounds bounds, float hue, float padding, Color color) {

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

        nvgRGBA((byte) innerBorder.getRed(), (byte) innerBorder.getGreen(), (byte) innerBorder.getBlue(), (byte) innerBorder.getAlpha(), innerOrange);

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
        nvgRoundedRect(CONTEXT, bounds.getX() + inset, bounds.getY() + inset, bounds.getWidth() - inset * 2, bounds.getHeight() - inset * 2, radius);
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
    public void beginScissors(ScreenBounds bounds, boolean gui) {
        nvgSave(CONTEXT);
        if (gui)
            nvgScissor(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height);
        else {
            nvgIntersectScissor(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    // (Rest of your methods remain unchanged — formatting continues consistently)
}