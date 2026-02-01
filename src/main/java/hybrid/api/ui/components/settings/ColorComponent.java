package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;

import java.awt.*;

public class ColorComponent extends HybridComponent {

    private static final int HEIGHT = 100;

    private final ColorSetting colorSetting;

    private float hue;
    private float saturation;
    private float value;

    private float svU = 1f;
    private float svV = 0f;

    private boolean dragging = false;
    private DragMode dragMode = DragMode.NONE;

    public ColorComponent(ColorSetting colorSetting) {
        this.colorSetting = colorSetting;

        Color c = colorSetting.get();
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hue = hsv[0];
        saturation = hsv[1];
        value = hsv[2];

        svU = saturation;
        svV = clamp(value - saturation);
    }

    @Override
    public void setupBounds() {
        componentBounds.setSize(componentBounds.getWidth(), HEIGHT);
    }



    @Override
    public void render(HybridRenderer renderer) {

        renderer.drawQuad(componentBounds, Theme.modBackgroundColor);

        int pickerSize = HEIGHT - 12;

        ScreenBounds pickerBounds = new ScreenBounds(componentBounds.getX() + 6, componentBounds.getY() + 6, pickerSize, pickerSize);

        renderer.drawColorTriangle(pickerBounds, hue, 6f);

        float cx = pickerBounds.getX() + pickerBounds.getWidth() / 2f;
        float cy = pickerBounds.getY() + pickerBounds.getHeight() / 2f;
        float radius = pickerBounds.getWidth() / 2f;
        float inner = radius * 0.78f;

        ScreenBounds hueSel = getHueSelectorBounds(cx, cy, radius);
        renderer.drawCircle(hueSel, Color.WHITE);
        renderer.drawOutlineQuad(hueSel, new Color(0, 0, 0, 0), Color.BLACK, 4, 1);

        ScreenBounds svSel = getSVSelectorBounds(cx, cy, inner);
        renderer.drawCircle(svSel, Color.WHITE);
        renderer.drawOutlineQuad(svSel, new Color(0, 0, 0, 0), Color.BLACK, 4, 1);

        ScreenBounds preview = new ScreenBounds(pickerBounds.getX() + pickerBounds.getWidth() + 10, pickerBounds.getY(), pickerSize, pickerSize);

        renderer.drawQuad(preview, colorSetting.get(), 6);

        HybridTextRenderer.addText(colorSetting.get().toString(), FontStyle.BOLD, 16, componentBounds.getX(), componentBounds.getY(), Color.WHITE);
    }


    @Override
    public void onMouseClicked(Click click) {

        if (!componentBounds.contains((int) click.x(), (int) click.y())) return;

        dragging = true;
        dragMode = DragMode.NONE;

        int size = HEIGHT - 12;
        float cx = componentBounds.getX() + 6 + size / 2f;
        float cy = componentBounds.getY() + 6 + size / 2f;

        float dx = (float) (click.x() - cx);
        float dy = (float) (click.y() - cy);
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        float radius = size / 2f;
        float inner = radius * 0.78f;

        if (dist <= radius && dist >= inner) {
            dragMode = DragMode.HUE;
            updateHue(click.x(), click.y(), cx, cy);
            applyColor();
        } else if (dist < inner) {
            dragMode = DragMode.SV;
            pickSV(click.x(), click.y(), cx, cy, inner);
            applyColor();
        }
    }



    @Override
    public void onMouseDrag(Click click) {

        if (!dragging) return;

        int size = HEIGHT - 12;
        float cx = componentBounds.getX() + 6 + size / 2f;
        float cy = componentBounds.getY() + 6 + size / 2f;
        float inner = (size / 2f) * 0.78f;

        if (dragMode == DragMode.HUE) {
            updateHue(click.x(), click.y(), cx, cy);
            applyColor();
        } else if (dragMode == DragMode.SV) {
            pickSV(click.x(), click.y(), cx, cy, inner);
            applyColor();
        }
    }

    @Override
    public void onMouseRelease(Click click) {
        dragging = false;
        dragMode = DragMode.NONE;
    }

    private void updateHue(double mx, double my, float cx, float cy) {
        float angle = (float) Math.atan2(my - cy, mx - cx);
        hue = angle / (float) (Math.PI * 2.0);
        if (hue < 0) hue += 1f;
    }

    private void pickSV(double mx, double my, float cx, float cy, float radius) {

        float a = hue * (float) (Math.PI * 2.0);

        float ax = cx + (float) Math.cos(a) * radius;
        float ay = cy + (float) Math.sin(a) * radius;
        float bx = cx + (float) Math.cos(a + 2f * Math.PI / 3f) * radius;
        float by = cy + (float) Math.sin(a + 2f * Math.PI / 3f) * radius;
        float cx2 = cx + (float) Math.cos(a + 4f * Math.PI / 3f) * radius;
        float cy2 = cy + (float) Math.sin(a + 4f * Math.PI / 3f) * radius;

        float px = (float) mx;
        float py = (float) my;

        float v0x = bx - ax, v0y = by - ay;
        float v1x = cx2 - ax, v1y = cy2 - ay;
        float v2x = px - ax, v2y = py - ay;

        float d00 = v0x * v0x + v0y * v0y;
        float d01 = v0x * v1x + v0y * v1y;
        float d11 = v1x * v1x + v1y * v1y;
        float d20 = v2x * v0x + v2y * v0y;
        float d21 = v2x * v1x + v2y * v1y;

        float denom = d00 * d11 - d01 * d01;
        if (denom == 0) return;

        float v = (d11 * d20 - d01 * d21) / denom;
        float w = (d00 * d21 - d01 * d20) / denom;
        float u = 1f - v - w;

        u = clamp(u);
        v = clamp(v);
        w = clamp(w);

        float sum = u + v + w;
        if (sum != 0f) {
            u /= sum;
            v /= sum;
        }

        svU = u;
        svV = v;

        saturation = clamp(svU);
        value = clamp(svU + svV);
    }

    private void applyColor() {
        colorSetting.set(Color.getHSBColor(hue, saturation, value));
    }

    private float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    private ScreenBounds getHueSelectorBounds(float cx, float cy, float radius) {
        float a = hue * (float) (Math.PI * 2.0);
        float r = radius * 0.89f;
        return new ScreenBounds((int) (cx + Math.cos(a) * r - 4), (int) (cy + Math.sin(a) * r - 4), 8, 8);
    }

    private ScreenBounds getSVSelectorBounds(float cx, float cy, float radius) {

        float a = hue * (float) (Math.PI * 2.0);

        float ax = cx + (float) Math.cos(a) * radius;
        float ay = cy + (float) Math.sin(a) * radius;
        float bx = cx + (float) Math.cos(a + 2f * Math.PI / 3f) * radius;
        float by = cy + (float) Math.sin(a + 2f * Math.PI / 3f) * radius;
        float cx2 = cx + (float) Math.cos(a + 4f * Math.PI / 3f) * radius;
        float cy2 = cy + (float) Math.sin(a + 4f * Math.PI / 3f) * radius;

        float w = 1f - svU - svV;

        float px = ax * svU + bx * svV + cx2 * w;
        float py = ay * svU + by * svV + cy2 * w;

        return new ScreenBounds((int) (px - 4), (int) (py - 4), 8, 8);
    }

    private enum DragMode {
        NONE, HUE, SV
    }
}
