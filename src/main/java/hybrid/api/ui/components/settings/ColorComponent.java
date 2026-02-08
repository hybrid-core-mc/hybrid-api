package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;

import java.awt.*;

public class ColorComponent extends HybridComponent {

    private final int HEIGHT = 100;

    private final float TRIANGLE_RATIO = 0.75f;
    private final float CENTER_Y_OFFSET = 5f;

    private final float PICKER_PADDING = 6f;

    private final ColorSetting colorSetting;

    private float hue;
    private float saturation;
    private float value;

    private boolean dragging = false;
    private DragMode dragMode = DragMode.NONE;

    private ScreenBounds pickerBounds;

    private float bcU = 1f;
    private float bcV = 0f;

    private float triAx, triAy;
    private float triBx, triBy;
    private float triCx, triCy;

    public ColorComponent(ColorSetting colorSetting) {
        this.colorSetting = colorSetting;

        Color c = colorSetting.get();
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];
    }

    @Override
    public void setupBounds() {
        componentBounds.setSize(componentBounds.getWidth(), HEIGHT);
    }

    @Override
    public void render(HybridRenderer renderer) {

        int pickerSize = HEIGHT - 25;
        int centerY = componentBounds.getY() + (componentBounds.getHeight() - pickerSize) / 2;

        pickerBounds = new ScreenBounds(
                componentBounds.getX(),
                centerY,
                pickerSize,
                pickerSize
        );

        ScreenBounds preview = new ScreenBounds(
                componentBounds.getX() + componentBounds.getWidth() - pickerSize,
                centerY,
                pickerSize,
                pickerSize
        );

        pickerBounds.setX(preview.getX());

        renderer.drawOutlineQuad(preview,
                Theme.modsBackgroundColor,
                Theme.modButtonOutlineColor,
                6,
                1
        );

        renderer.drawColorTriangle(pickerBounds, hue, PICKER_PADDING);

        float cx = pickerBounds.getX() + pickerBounds.getWidth() / 2f;
        float cy = pickerBounds.getY() + pickerBounds.getHeight() / 2f + CENTER_Y_OFFSET;

        float baseRadius = (Math.min(pickerBounds.getWidth(), pickerBounds.getHeight()) / 2f) - PICKER_PADDING;
        if (baseRadius <= 0f) return;

        float triRadius = baseRadius * TRIANGLE_RATIO;

        updateTriangle(cx, cy, triRadius);


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
        String hexText = String.format("#%02X%02X%02X%02X",
                c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()
        );

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


    private void updateTriangle(float cx, float cy, float radius) {
        float a = hue * (float) (Math.PI * 2.0);

        float ax = cx + (float) Math.cos(a) * radius;
        float ay = cy + (float) Math.sin(a) * radius;

        float bx = cx + (float) Math.cos(a + 2f * (float) Math.PI / 3f) * radius;
        float by = cy + (float) Math.sin(a + 2f * (float) Math.PI / 3f) * radius;

        float cx2 = cx + (float) Math.cos(a + 4f * (float) Math.PI / 3f) * radius;
        float cy2 = cy + (float) Math.sin(a + 4f * (float) Math.PI / 3f) * radius;

        triAx = ax;
        triAy = ay;
        triBx = bx;
        triBy = by;
        triCx = cx2;
        triCy = cy2;
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

        if (pickerBounds == null) return;
        if (!pickerBounds.contains((int) click.x(), (int) click.y())) return;

        dragging = true;
        dragMode = DragMode.NONE;

        float cx = pickerBounds.getX() + pickerBounds.getWidth() / 2f;
        float cy = pickerBounds.getY() + pickerBounds.getHeight() / 2f + CENTER_Y_OFFSET;

        float baseRadius = (Math.min(pickerBounds.getWidth(), pickerBounds.getHeight()) / 2f) - PICKER_PADDING;
        if (baseRadius <= 0f) return;

        float HUE_RING_RATIO = 0.78f;
        float inner = baseRadius * HUE_RING_RATIO;
        float triR = baseRadius * TRIANGLE_RATIO;

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

        if (!dragging || pickerBounds == null) return;

        float cx = pickerBounds.getX() + pickerBounds.getWidth() / 2f;
        float cy = pickerBounds.getY() + pickerBounds.getHeight() / 2f + CENTER_Y_OFFSET;

        float baseRadius = (Math.min(pickerBounds.getWidth(), pickerBounds.getHeight()) / 2f) - PICKER_PADDING;
        if (baseRadius <= 0f) return;

        float triR = baseRadius * TRIANGLE_RATIO;

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

        if (u < 0f) { float t = v / (v + w); v = t; w = 1f - t; u = 0f; }
        if (v < 0f) { float t = u / (u + w); u = t; w = 1f - t; v = 0f; }
        if (w < 0f) { float t = u / (u + v); u = t; v = 1f - t; w = 0f; }

        bcU = u;
        bcV = v;

        saturation = clamp(1f - u);
        value = clamp(1f - v);
    }

    private void applyColor() {
        colorSetting.set(Color.getHSBColor(hue, saturation, value));
    }

    private float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private enum DragMode {
        NONE, HUE, SV
    }
}