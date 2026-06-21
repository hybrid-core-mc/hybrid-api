package hybrid.api.util.render;

import hybrid.api.mod.settings.ColorSetting;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class HueCirclePicker {


    final float maxClickRadiusSq, minClickRadiusSq;
    final ColorSetting colorSetting;
    private final int radius;
    public float x, y;
    float selectedHue = 0.0f;

    public boolean isDragging = false;

    public HueCirclePicker(int radius, int normalizedRadius, ColorSetting setting) {
        this.radius = radius;
        this.colorSetting = setting;

        this.minClickRadiusSq = (float) (normalizedRadius * normalizedRadius);
        float outerRadius = normalizedRadius + 4.0f;
        this.maxClickRadiusSq = outerRadius * outerRadius;
    }

    public void render(Quad quad) {
        this.x = quad.x;
        this.y = quad.y;


        HybridRenderer2D.drawHueCircle(
                new Quad((int) x - radius, (int) y - radius, 0, 0),
                radius,
                colorSetting.get()
        );
    }

    public void mouseClicked(MouseButtonEvent event) {
        if (event.button() == 0) {
            float mouseX = (float) event.x();
            float mouseY = (float) event.y();

            float dx = mouseX - x;
            float dy = mouseY - y;
            float distSq = dx * dx + dy * dy;

            if (distSq >= minClickRadiusSq && distSq <= maxClickRadiusSq) {
                this.isDragging = true;
                updateHue(mouseX, mouseY);
            }
        }
    }

    public void mouseDragged(MouseButtonEvent event) {
        if (isDragging) {
            updateHue((float) event.x(), (float) event.y());
        }
    }

    public void mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            this.isDragging = false;
        }
    }

    private void updateHue(float mouseX, float mouseY) {
        float dx = mouseX - x;
        float dy = mouseY - y;
        float angle = (float) Math.atan2(dy, dx);

        float hue = angle / ((float) (Math.PI * 2.0));
        hue += 0.25f;
        hue -= (float) Math.floor(hue);

        this.selectedHue = hue;

        int currentAlpha = colorSetting.get().getAlpha();
        Color hsbColor = Color.getHSBColor(selectedHue, 1.0f, 1.0f);

        colorSetting.set(new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), currentAlpha));
    }
}