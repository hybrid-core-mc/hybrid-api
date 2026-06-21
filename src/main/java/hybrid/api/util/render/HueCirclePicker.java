package hybrid.api.util.render;

import hybrid.api.mod.settings.ColorSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class HueCirclePicker {

    private final int radius; // The radius utilized by the shader renderer
    private final int ghostRadius; // The custom radius used for calculation bounds and visuals

    public float selectedHue = 0.0f;
    ColorSetting colorSetting;

    public float centerX;
    public float centerY;
    public boolean isDragging = false;

    // Added ghostRadius to the constructor so you can pass both values from ColorComponent
    public HueCirclePicker(int radius, int ghostRadius, ColorSetting setting) {
        this.radius = radius;
        this.ghostRadius = ghostRadius;
        this.colorSetting = setting;
    }

    public void draw(float cx, float cy, Color currentValue) {

        this.centerX = cx;
        this.centerY = cy;

        int normalized = radius;
        HybridRenderer2D.drawHueCircle(
                new Quad((int) cx-normalized, (int) cy-normalized , 0, 0),
                radius,
                currentValue
        );


        int clickInnerRadius = ghostRadius;
        int clickOuterRadius = ghostRadius + 4;

        float innerMin = (float) (clickInnerRadius * clickInnerRadius);
        float outerMax = (float) (clickOuterRadius * clickOuterRadius);
/*
        if (RenderContext.get() != null) {
            int startX = (int) (centerX - clickOuterRadius);
            int endX = (int) (centerX + clickOuterRadius);
            int startY = (int) (centerY - clickOuterRadius);
            int endY = (int) (centerY + clickOuterRadius);

            int debugColor = new Color(255, 0, 0, 75).getRGB(); // Transparent red

            for (int px = startX; px <= endX; px++) {
                for (int py = startY; py <= endY; py++) {
                    float dx = px - centerX;
                    float dy = py - centerY;
                    float distSq = dx * dx + dy * dy;

                    // Displays where the new ghost calculations think your ring is
                    if (distSq >= innerMin && distSq <= outerMax) {
                        RenderContext.get().fill(px, py, px + 1, py + 1, debugColor);
                    }
                }
            }
        }*/
    }

    public void mouseClicked(MouseButtonEvent event) {
        if (event.button() == 0) {
            float mouseX = (float) event.x();
            float mouseY = (float) event.y();

            float dx = mouseX - centerX;
            float dy = mouseY - centerY;
            float distSq = dx * dx + dy * dy;

            int clickInnerRadius = ghostRadius;
            int clickOuterRadius = ghostRadius + 4;
            float innerMin = (float) (clickInnerRadius * clickInnerRadius);
            float outerMax = (float) (clickOuterRadius * clickOuterRadius);

            if (distSq >= innerMin && distSq <= outerMax) {
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
        float dx = mouseX - centerX;
        float dy = mouseY - centerY;
        float angle = (float) Math.atan2(dy, dx);

        float hue = angle / ((float)(Math.PI * 2.0));
        hue += 0.25f;
        hue -= (float) Math.floor(hue);

        selectedHue = hue;

        int currentAlpha = colorSetting.get().getAlpha();
        Color hsbColor = Color.getHSBColor(selectedHue, 1.0f, 1.0f);
        colorSetting.set(new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), currentAlpha));
    }
}