package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.animation.PositionAnimation;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;

import java.awt.*;

public class ColorComponent extends HybridComponent {

    static final int HEIGHT = 100;

    final ColorSetting colorSetting;

    float hue;
    float saturation;
    float value;
    float alpha;
    PositionAnimation alphaAnim = new PositionAnimation(1f, 18f);
    boolean dragging = false;
    DragMode dragMode = DragMode.NONE;

    ScreenBounds pickerBounds;
    ScreenBounds alphaBoundsShared;

    float bcU = 1f;
    float bcV = 0f;

    float triAx, triAy;
    float triBx, triBy;
    float triCx, triCy;

    public ColorComponent(ColorSetting colorSetting) {
        this.colorSetting = colorSetting;

        Color c = colorSetting.get();
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
        alpha = c.getAlpha() / 255f;
        alphaAnim.snap(alpha);

        syncTriangleFromSV();
    }
    private void syncTriangleFromSV() {
        bcU = clamp(1f - saturation);
        bcV = clamp(1f - value);

        float sum = bcU + bcV;
        if (sum > 1f) {
            bcU /= sum;
            bcV /= sum;
        }
    }
    @Override
    public void setupBounds() {
        componentBounds.setSize(componentBounds.getWidth(), HEIGHT);
    }

    @Override
    public void render(HybridRenderer renderer) {
        int pickerSize = HEIGHT - 25;
        int centerY = componentBounds.getY() + (componentBounds.getHeight() - pickerSize) / 2;
        alphaAnim.update();
        ScreenBounds preview = new ScreenBounds(
                componentBounds.getX() + componentBounds.getWidth() - pickerSize,
                centerY,
                pickerSize,
                pickerSize
        );

        pickerBounds = preview.copy();

        renderer.drawOutlineQuad(preview,
                Theme.modsBackgroundColor,
                Theme.modButtonOutlineColor,
                6,
                1
        );

        float pickerPadding = 6f;
        renderer.drawColorTriangle(pickerBounds, hue, pickerPadding);

        float cx = pickerBounds.getX() + pickerBounds.getWidth() / 2f;
        float cy = pickerBounds.getY() + pickerBounds.getHeight() / 2f + 5f;

        float baseRadius = (Math.min(pickerBounds.getWidth(), pickerBounds.getHeight()) / 2f) - pickerPadding;
        if (baseRadius <= 0f) return;

        float ringInner = baseRadius * 0.78f;
        float triRadius = baseRadius * 0.75f;

        updateTriangle(cx, cy, triRadius);
        drawHueIndicator(renderer, cx, cy, ringInner, baseRadius);

        ScreenBounds svSel = getSVSelectorBoundsFromShared();
        renderer.drawCircle(svSel, Color.WHITE);
        renderer.drawOutlineQuad(svSel,
                new Color(0, 0, 0, 0),
                Color.BLACK,
                4,
                1
        );

        HybridRenderText label = HybridTextRenderer.getTextRenderer(
                colorSetting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );
        label.setPosition(componentBounds.getX(), pickerBounds.getY());
        HybridTextRenderer.addText(label);

        ScreenBounds alphaBounds = componentBounds.copy();
        int alphaHeight = 20;

        alphaBounds.setPosition(
                alphaBounds.getX() - 1,
                pickerBounds.getY() + pickerBounds.getHeight() - alphaHeight
        );
        alphaBounds.setSize(
                (int) ((componentBounds.getX() - pickerBounds.getWidth()) * 0.54),
                alphaHeight
        );

        renderer.drawAlphaSlider(alphaBounds, colorSetting.get());
        alphaBoundsShared = alphaBounds.copy();

        drawAlphaIndicator(renderer);

        ScreenBounds colorBounds = alphaBounds.copy();
        colorBounds.setHeight(colorBounds.getHeight() + 5);
        colorBounds.setY(colorBounds.getY() - (colorBounds.getHeight() + 8));

        renderer.drawOutlineQuad(
                colorBounds,
                Theme.modBackgroundColor,
                Theme.modButtonOutlineColor,
                5,
                1
        );

        Color c = colorSetting.get();
        String rgbText = c.getRed() + "   " + c.getGreen() + "   " + c.getBlue() + "   " + c.getAlpha();
        String hexText = String.format("#%02X%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());

        HybridRenderText rgbLabel = HybridTextRenderer.getTextRenderer(
                rgbText,
                FontStyle.BOLD,
                18,
                Color.LIGHT_GRAY,
                new Color(140, 140, 140, 255),
                true
        );
        HybridRenderText hexLabel = HybridTextRenderer.getTextRenderer(
                hexText,
                FontStyle.BOLD,
                18,
                Color.LIGHT_GRAY,
                new Color(140, 140, 140, 255),
                true
        );

        int textY = colorBounds.getY() + (colorBounds.getHeight() - rgbLabel.getHeight()) / 2;

        int padding = 8;
        int dividerWidth = 2;
        int dividerHeight = colorBounds.getHeight() - 8;

        int rgbX = colorBounds.getX() + padding;
        rgbLabel.setPosition(rgbX, textY);
        HybridTextRenderer.addText(rgbLabel);

        int hexX = colorBounds.getX() + colorBounds.getWidth() - padding - hexLabel.getWidth();
        hexLabel.setPosition(hexX, textY);
        HybridTextRenderer.addText(hexLabel);

        int rgbRight = rgbX + rgbLabel.getWidth();
        int dividerX = rgbRight + (hexX - rgbRight - dividerWidth) / 2;

        renderer.drawQuad(
                new ScreenBounds(dividerX, colorBounds.getY() + 4, dividerWidth, dividerHeight),
                Theme.modButtonOutlineColor
        );
    }

    private void drawHueIndicator(HybridRenderer renderer, float cx, float cy, float inner, float outer) {
        float a = hue * (float) (Math.PI * 2.0f);

        float half = 3.0f * 0.5f;
        float inset = 0.8f;
        float extra = 0.75f;

        float r1 = (inner - half - extra) + inset;
        float r2 = (outer + half + extra) - inset;

        float x1 = cx + (float) Math.cos(a) * r1;
        float y1 = cy + (float) Math.sin(a) * r1;

        float x2 = cx + (float) Math.cos(a) * r2;
        float y2 = cy + (float) Math.sin(a) * r2;

        ScreenBounds line = lineEndpoints(x1, y1, x2, y2);

        renderer.drawLine(line, new Color(0, 0, 0, 160), 4.0f);
        renderer.drawLine(line, Color.WHITE, 3.0f);
    }

    private void drawAlphaIndicator(HybridRenderer renderer) {
        if (alphaBoundsShared == null) return;

        float t = clamp(alphaAnim.get());
        float knobOuterR = 7.0f;
        float knobRing = 2.0f;
        float insetX = 2.0f;

        float minX = alphaBoundsShared.getX() + insetX + knobOuterR;
        float maxX = alphaBoundsShared.getX() + alphaBoundsShared.getWidth() - insetX - knobOuterR;

        float x = minX + (maxX - minX) * t;
        float y = alphaBoundsShared.getY() + alphaBoundsShared.getHeight() / 2f;

        float innerR = Math.max(1f, knobOuterR - knobRing);

        ScreenBounds outer = new ScreenBounds(Math.round(x - knobOuterR), Math.round(y - knobOuterR), Math.round(knobOuterR * 2f), Math.round(knobOuterR * 2f));

        ScreenBounds inner = new ScreenBounds(Math.round(x - innerR), Math.round(y - innerR), Math.round(innerR * 2f), Math.round(innerR * 2f));

        int rgb = Color.HSBtoRGB(hue, saturation, value);
        Color fill = new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, 255);

        renderer.drawCircle(outer, Color.WHITE);
        renderer.drawCircle(inner, fill);
    }

    private ScreenBounds lineEndpoints(float x1, float y1, float x2, float y2) {
        return new ScreenBounds(Math.round(x1), Math.round(y1), Math.round(x2), Math.round(y2));
    }

    private void updateTriangle(float cx, float cy, float radius) {
        float a = hue * (float) (Math.PI * 2.0f);

        triAx = cx + (float) Math.cos(a) * radius;
        triAy = cy + (float) Math.sin(a) * radius;

        triBx = cx + (float) Math.cos(a + 2f * (float) Math.PI / 3f) * radius;
        triBy = cy + (float) Math.sin(a + 2f * (float) Math.PI / 3f) * radius;

        triCx = cx + (float) Math.cos(a + 4f * (float) Math.PI / 3f) * radius;
        triCy = cy + (float) Math.sin(a + 4f * (float) Math.PI / 3f) * radius;
    }

    private ScreenBounds getSVSelectorBoundsFromShared() {
        float w = 1f - bcU - bcV;
        float px = triAx * bcU + triBx * bcV + triCx * w;
        float py = triAy * bcU + triBy * bcV + triCy * w;

        return new ScreenBounds(
                Math.round(px - 4),
                Math.round(py - 4),
                8,
                8
        );
    }

    @Override
    public void onMouseClicked(Click click) {
        if (alphaBoundsShared != null && alphaBoundsShared.contains((int) click.x(), (int) click.y())) {
            dragging = true;
            dragMode = DragMode.ALPHA;
            updateAlpha(click.x());
            applyColor();
            return;
        }

        if (pickerBounds == null) return;
        if (!pickerBounds.contains((int) click.x(), (int) click.y())) return;

        dragging = true;
        dragMode = DragMode.NONE;

        float pickerPadding = 6f;

        float cx = pickerBounds.getX() + pickerBounds.getWidth() / 2f;
        float cy = pickerBounds.getY() + pickerBounds.getHeight() / 2f + 5f;

        float baseRadius = (Math.min(pickerBounds.getWidth(), pickerBounds.getHeight()) / 2f) - pickerPadding;
        if (baseRadius <= 0f) return;

        float inner = baseRadius * 0.78f;
        float triR = baseRadius * 0.75f;

        float dx = (float) (click.x() - cx);
        float dy = (float) (click.y() - cy);
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist <= baseRadius && dist >= inner) {
            dragMode = DragMode.HUE;
            updateHue(click.x(), click.y(), cx, cy);
        } else if (dist < inner) {
            dragMode = DragMode.SV;
            updateTriangle(cx, cy, triR);
            pickSVUsingShared(click.x(), click.y());
        }

        applyColor();
    }

    @Override
    public void onMouseDrag(Click click) {
        if (!dragging) return;

        if (dragMode == DragMode.ALPHA) {
            updateAlpha(click.x());
            applyColor();
            return;
        }

        if (pickerBounds == null) return;

        float pickerPadding = 6f;

        float cx = pickerBounds.getX() + pickerBounds.getWidth() / 2f;
        float cy = pickerBounds.getY() + pickerBounds.getHeight() / 2f + 5f;

        float baseRadius = (Math.min(pickerBounds.getWidth(), pickerBounds.getHeight()) / 2f) - pickerPadding;
        if (baseRadius <= 0f) return;

        float triR = baseRadius * 0.75f;

        if (dragMode == DragMode.HUE) {
            updateHue(click.x(), click.y(), cx, cy);
            updateTriangle(cx, cy, triR);
        } else if (dragMode == DragMode.SV) {
            updateTriangle(cx, cy, triR);
            pickSVUsingShared(click.x(), click.y());
        }

        applyColor();
    }

    @Override
    public void onMouseRelease(Click click) {
        dragging = false;
        dragMode = DragMode.NONE;
    }

    private void updateHue(double mx, double my, float cx, float cy) {
        float angle = (float) Math.atan2(my - cy, mx - cx);
        hue = angle / (float) (Math.PI * 2.0f);
        if (hue < 0) hue += 1f;
    }

    private void updateAlpha(double mx) {
        if (alphaBoundsShared == null) return;

        float t = ((float) mx - alphaBoundsShared.getX()) / alphaBoundsShared.getWidth();
        alpha = clamp(t);

        alphaAnim.setTarget(alpha);
    }

    private void pickSVUsingShared(double mx, double my) {
        float ax = triAx, ay = triAy;
        float bx = triBx, by = triBy;
        float cx = triCx, cy = triCy;

        float px = (float) mx;
        float py = (float) my;

        float v0x = bx - ax, v0y = by - ay;
        float v1x = cx - ax, v1y = cy - ay;
        float v2x = px - ax, v2y = py - ay;

        float d00 = v0x * v0x + v0y * v0y;
        float d01 = v0x * v1x + v0y * v1y;
        float d11 = v1x * v1x + v1y * v1y;
        float d20 = v2x * v0x + v2y * v0y;
        float d21 = v2x * v1x + v2y * v1y;

        float denom = d00 * d11 - d01 * d01;
        if (denom == 0f) return;

        float v = (d11 * d20 - d01 * d21) / denom;
        float w = (d00 * d21 - d01 * d20) / denom;
        float u = 1f - v - w;

        if (u < 0f) {
            float tt = v / (v + w);
            v = tt;
            w = 1f - tt;
            u = 0f;
        }
        if (v < 0f) {
            float tt = u / (u + w);
            u = tt;
            w = 1f - tt;
            v = 0f;
        }
        if (w < 0f) {
            float tt = u / (u + v);
            u = tt;
            v = 1f - tt;
            w = 0f;
        }

        bcU = u;
        bcV = v;

        saturation = clamp(1f - u);
        value = clamp(1f - v);
    }

    private void applyColor() {
        int rgb = Color.HSBtoRGB(hue, saturation, value);

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb) & 0xFF;
        int a = Math.round(alpha * 255f);
        colorSetting.set(new Color(r, g, b, a));
    }

    private float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private enum DragMode {
        NONE, HUE, SV, ALPHA
    }
}