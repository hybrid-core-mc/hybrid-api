package hybrid.api.rendering;

import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

import java.awt.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class ColorPickerRenderer {

    private final long vg;

    private final NVGColor colorA = NVGColor.create();
    private final NVGColor colorB = NVGColor.create();
    private final NVGPaint paintA = NVGPaint.create();
    private final NVGPaint paintB = NVGPaint.create();

    public ColorPickerRenderer(long CONTEXT) {
        this.vg = CONTEXT;
    }


    public void drawColorPicker(ScreenBounds bounds, float hue, float padding) {

        float cx = bounds.x + bounds.width / 2f;
        float cy = bounds.y + bounds.height / 2f;

        float radius = (Math.min(bounds.width, bounds.height) / 2f) - padding;
        if (radius <= 0) return;

        drawColorPickerInternal(cx, cy, radius, hue);
    }



    private void drawColorPickerInternal(float cx, float cy, float radius, float hue) {
        drawHueRing(cx, cy, radius);
        drawColorTriangle(cx, cy, radius * 0.75f, hue);
    }


    private void drawHueRing(float cx, float cy, float radius) {

        float inner = radius * 0.78f;

        for (int i = 0; i < 360; i++) {

            float a0 = (float) Math.toRadians(i);
            float a1 = (float) Math.toRadians(i + 1);

            Color c0 = hsvToRgb(i / 360f, 1f, 1f);
            Color c1 = hsvToRgb((i + 1) / 360f, 1f, 1f);

            rgba(c0, colorA);
            rgba(c1, colorB);

            nvgLinearGradient(vg, cx + (float) Math.cos(a0) * inner, cy + (float) Math.sin(a0) * inner, cx + (float) Math.cos(a0) * radius, cy + (float) Math.sin(a0) * radius, colorA, colorB, paintA);

            nvgBeginPath(vg);
            nvgArc(vg, cx, cy, radius, a0, a1, NVG_CW);
            nvgArc(vg, cx, cy, inner, a1, a0, NVG_CCW);
            nvgClosePath(vg);
            nvgFillPaint(vg, paintA);
            nvgFill(vg);
        }
    }



    private void drawColorTriangle(float cx, float cy, float radius, float hue) {

        float angle = hue * (float) (Math.PI * 2.0);

        float ax = cx + (float) Math.cos(angle) * radius;
        float ay = cy + (float) Math.sin(angle) * radius;

        float bx = cx + (float) Math.cos(angle + 2f * Math.PI / 3f) * radius;
        float by = cy + (float) Math.sin(angle + 2f * Math.PI / 3f) * radius;

        float cx2 = cx + (float) Math.cos(angle + 4f * Math.PI / 3f) * radius;
        float cy2 = cy + (float) Math.sin(angle + 4f * Math.PI / 3f) * radius;

        Color hueColor = hsvToRgb(hue, 1f, 1f);
        rgba(255, 255, 255, 255, colorA);
        rgba(hueColor, colorB);

        nvgLinearGradient(vg, bx, by, ax, ay, colorA, colorB, paintA);

        nvgBeginPath(vg);
        nvgMoveTo(vg, ax, ay);
        nvgLineTo(vg, bx, by);
        nvgLineTo(vg, cx2, cy2);
        nvgClosePath(vg);
        nvgFillPaint(vg, paintA);
        nvgFill(vg);

        rgba(0, 0, 0, 0, colorA);
        rgba(0, 0, 0, 255, colorB);

        nvgLinearGradient(vg, cx2, cy2, (ax + bx) * 0.5f, (ay + by) * 0.5f, colorA, colorB, paintB);

        nvgBeginPath(vg);
        nvgMoveTo(vg, ax, ay);
        nvgLineTo(vg, bx, by);
        nvgLineTo(vg, cx2, cy2);
        nvgClosePath(vg);
        nvgFillPaint(vg, paintB);
        nvgFill(vg);

        nvgBeginPath(vg);
        nvgMoveTo(vg, ax, ay);
        nvgLineTo(vg, bx, by);
        nvgLineTo(vg, cx2, cy2);
        nvgClosePath(vg);
        rgba(0, 0, 0, 160, colorA);
        nvgStrokeColor(vg, colorA);
        nvgStrokeWidth(vg, 1.0f);
        nvgStroke(vg);
    }


    private void rgba(Color c, NVGColor out) {
        rgba(c.getRed(), c.getGreen(), c.getBlue(), 255, out);
    }

    private void rgba(int r, int g, int b, int a, NVGColor out) {
        nvgRGBA((byte) r, (byte) g, (byte) b, (byte) a, out);
    }

    private Color hsvToRgb(float h, float s, float v) {
        return new Color(Color.HSBtoRGB(h, s, v));
    }
}
