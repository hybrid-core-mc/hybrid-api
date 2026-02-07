package hybrid.api.rendering;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import java.awt.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class ColorPickerRenderer {

    private static final float HUE_RING_RATIO = 0.78f;
    private static final float TRIANGLE_RATIO = 0.75f;
    private static final float CENTER_Y_OFFSET = 5f;

    private final long CONTEXT;

    private final NVGColor cA = NVGColor.create();
    private final NVGColor cB = NVGColor.create();
    private final NVGPaint paintA = NVGPaint.create();
    private final NVGPaint paintB = NVGPaint.create();

    public ColorPickerRenderer(long CONTEXT) {
        this.CONTEXT = CONTEXT;
    }


    public void drawColorPicker(ScreenBounds bounds, float hue, float padding) {

        float cx = bounds.x + bounds.width / 2f;
        float cy = bounds.y + bounds.height / 2f + CENTER_Y_OFFSET;

        float radius = (Math.min(bounds.width, bounds.height) / 2f) - padding;
        if (radius <= 0f) return;

        drawHueRing(cx, cy, radius);
        drawColorTriangle(cx, cy, radius * TRIANGLE_RATIO, hue);
    }


    private void drawHueRing(float cx, float cy, float radius) {

        float inner = radius * HUE_RING_RATIO;

        for (int i = 0; i < 720; i++) {

            float a0 = (float) Math.toRadians(i);
            float a1 = (float) Math.toRadians(i + 1);

            Color col0 = hsv(i / 360f);
            Color col1 = hsv((i + 1) / 360f);

            rgba(col0, cA);
            rgba(col1, cB);

            nvgLinearGradient(CONTEXT, cx + (float) Math.cos(a0) * inner, cy + (float) Math.sin(a0) * inner, cx + (float) Math.cos(a0) * radius, cy + (float) Math.sin(a0) * radius, cA, cB, paintA);

            nvgBeginPath(CONTEXT);
            nvgArc(CONTEXT, cx, cy, radius, a0, a1, NVG_CW);
            nvgArc(CONTEXT, cx, cy, inner, a1, a0, NVG_CCW);
            nvgClosePath(CONTEXT);

            nvgFillPaint(CONTEXT, paintA);
            nvgFill(CONTEXT);
        }
    }

    private void drawColorTriangle(float cx, float cy, float radius, float hue) {

        float a = hue * (float) (Math.PI * 2.0);

        float ax = cx + (float) Math.cos(a) * radius;
        float ay = cy + (float) Math.sin(a) * radius;

        float bx = cx + (float) Math.cos(a + 2f * Math.PI / 3f) * radius;
        float by = cy + (float) Math.sin(a + 2f * Math.PI / 3f) * radius;

        float cx2 = cx + (float) Math.cos(a + 4f * Math.PI / 3f) * radius;
        float cy2 = cy + (float) Math.sin(a + 4f * Math.PI / 3f) * radius;

        Color hueColor = hsv(hue);


        rgba(Color.WHITE, cA);
        rgba(Color.BLACK, cB);

        nvgLinearGradient(CONTEXT, ax, ay, bx, by, cA, cB, paintA);
        drawTriangle(ax, ay, bx, by, cx2, cy2, paintA);


        rgba(new Color(0, 0, 0, 0), cA);
        rgba(hueColor, cB);

        float mx = (ax + bx) * 0.5f;
        float my = (ay + by) * 0.5f;

        nvgLinearGradient(CONTEXT, cx2, cy2, mx, my, cB, cA, paintB);
        drawTriangle(ax, ay, bx, by, cx2, cy2, paintB);

        rgba(new Color(0, 0, 0, 160), cA);
        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, ax, ay);
        nvgLineTo(CONTEXT, bx, by);
        nvgLineTo(CONTEXT, cx2, cy2);
        nvgClosePath(CONTEXT);
        nvgStrokeColor(CONTEXT, cA);
        nvgStrokeWidth(CONTEXT, 1f);
        nvgStroke(CONTEXT);
    }

    private void drawTriangle(float ax, float ay, float bx, float by, float cx, float cy, NVGPaint paint) {
        nvgBeginPath(CONTEXT);
        nvgMoveTo(CONTEXT, ax, ay);
        nvgLineTo(CONTEXT, bx, by);
        nvgLineTo(CONTEXT, cx, cy);
        nvgClosePath(CONTEXT);
        nvgFillPaint(CONTEXT, paint);
        nvgFill(CONTEXT);
    }

    private void rgba(Color c, NVGColor out) {
        nvgRGBA((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue(), (byte) c.getAlpha(), out);
    }

    private Color hsv(float h) {
        return new Color(Color.HSBtoRGB(h, (float) 1.0, (float) 1.0));
    }
}