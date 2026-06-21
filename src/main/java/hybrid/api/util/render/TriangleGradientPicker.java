package hybrid.api.util.render;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import hybrid.api.mod.settings.ColorSetting;
import net.minecraft.client.input.MouseButtonEvent;
import java.awt.*;

public class TriangleGradientPicker {

    private final int scale;
    private final ColorSetting setting;

    private int x;
    private int y;
    private float rotation = 0.0f;

    public boolean isDragging = false;

    public TriangleGradientPicker(int scale, ColorSetting colorSetting) {
        this.scale = scale;
        this.setting = colorSetting;
    }

    public void render(Quad quad) {
        this.x = quad.x;
        this.y = quad.y;

        float angle = getAngleForColor(setting.get());
        this.rotation = angle + (float) Math.PI;

        Color currentSettingColor = setting.get() != null ? setting.get() : Color.GREEN;
        float[] hsb = Color.RGBtoHSB(currentSettingColor.getRed(), currentSettingColor.getGreen(), currentSettingColor.getBlue(), null);
        Color pureHueColor = Color.getHSBColor(hsb[0], 1.0f, 1.0f);

        HybridRenderer2D.drawTriangle(new Quad(x, y, scale, scale), pureHueColor, getAngleForColor(pureHueColor));

        if (isDragging && RenderContext.get() != null) {
            RenderContext.get().requestCursor(CursorTypes.CROSSHAIR);
        }
    }

    public void mouseClicked(MouseButtonEvent event) {
        if (event.button() == 0) {
            int clickX = (int) event.x();
            int clickY = (int) event.y();

            if (clickX >= x && clickX <= x + scale && clickY >= y && clickY <= y + scale) {
                this.isDragging = true;
                sampleColorAtMouse(clickX, clickY);
            }
        }
    }

    public void mouseDragged(MouseButtonEvent event) {
        if (this.isDragging) {
            sampleColorAtMouse((int) event.x(), (int) event.y());
        }
    }

    public void mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            this.isDragging = false;
        }
    }


    private void sampleColorAtMouse(int mouseX, int mouseY) {
        float centerX = x + (scale / 2.0f);
        float centerY = y + (scale / 2.0f);

        float r = 0.22f;
        float k = (float) Math.sqrt(3.0);
        float totalScale = scale * 1.11f;

        float v1x = (k * r * totalScale);
        float v1y = (r * totalScale);
        float v2x = 0;
        float v2y = -(r * 2.0f * totalScale);
        float v3x = (-k * r * totalScale);
        float v3y = (r * totalScale);

        float cos = (float) Math.cos(this.rotation);
        float sin = (float) Math.sin(this.rotation);

        float p1x = (v1x * cos - v1y * sin) + centerX;
        float p1y = (v1x * sin + v1y * cos) + centerY;
        float p2x = (v2x * cos - v2y * sin) + centerX;
        float p2y = (v2x * sin + v2y * cos) + centerY;
        float p3x = (v3x * cos - v3y * sin) + centerX;
        float p3y = (v3x * sin + v3y * cos) + centerY;

        float venom = (p2y - p3y) * (p1x - p3x) + (p3x - p2x) * (p1y - p3y);
        if (Math.abs(venom) < 0.0001f) return;

        boolean b1 = ((mouseX - p2x) * (p1y - p2y) - (p1x - p2x) * (mouseY - p2y)) < 0.0f;
        boolean b2 = ((mouseX - p3x) * (p2y - p3y) - (p2x - p3x) * (mouseY - p3y)) < 0.0f;
        boolean b3 = ((mouseX - p1x) * (p3y - p1y) - (p3x - p1x) * (mouseY - p1y)) < 0.0f;

        if ((b1 == b2) && (b2 == b3)) {
            float wWhite = ((p2y - p3y) * (mouseX - p3x) + (p3x - p2x) * (mouseY - p3y)) / venom;
            float wColor = ((p3y - p1y) * (mouseX - p3x) + (p1x - p3x) * (mouseY - p3y)) / venom;
            float wBlack = 1.0f - wColor - wWhite;

            wColor = Math.max(0.0f, Math.min(1.0f, wColor));
            wWhite = Math.max(0.0f, Math.min(1.0f, wWhite));
            wBlack = Math.max(0.0f, Math.min(1.0f, wBlack));

            Color currentSettingColor = setting.get() != null ? setting.get() : Color.GREEN;
            float[] hsb = Color.RGBtoHSB(currentSettingColor.getRed(), currentSettingColor.getGreen(), currentSettingColor.getBlue(), null);
            Color baseColor = Color.getHSBColor(hsb[0], 1.0f, 1.0f);

            int rMix = (int) ((wColor * baseColor.getRed()) + (wWhite * 255));
            int gMix = (int) ((wColor * baseColor.getGreen()) + (wWhite * 255));
            int bMix = (int) ((wColor * baseColor.getBlue()) + (wWhite * 255));

            int currentAlpha = setting.get().getAlpha();
            setting.set(new Color(rMix, gMix, bMix, currentAlpha));
        }
    }

    public float getAngleForColor(Color color) {
        if (color == null) return 0.0f;

        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        float angle = hsb[0] * (float) (Math.PI * 2.0);
        angle -= (float) (Math.PI / 2.0);

        return angle + 4.7f;
    }
}