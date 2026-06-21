package hybrid.api.util.render;

import hybrid.api.mod.settings.ColorSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class HueCirclePicker {

    private final int radius;

    public float selectedHue = 0.0f;
    ColorSetting colorSetting;

    public float centerX;
    public float centerY;

    public HueCirclePicker(int radius, ColorSetting setting) {
        this.radius = radius;
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
    }

    public void mouseClicked(MouseButtonEvent event) {
        float mouseX = (float) event.x();
        float mouseY = (float) event.y();

        int radiuz = radius - 11;
        float outerMax = (float) (radiuz * radiuz);

        float dx = mouseX - centerX;
        float dy = mouseY - centerY;
        float distSq = dx * dx + dy * dy;

        if (distSq > outerMax) {
            return;
        }

        float angle = (float) Math.atan2(dy, dx);

        float hue = angle / ((float)(Math.PI * 2.0));
        hue += 0.25f;
        hue -= (float) Math.floor(hue);

        selectedHue = hue;
        colorSetting.set(Color.getHSBColor(selectedHue, 1.0f, 1.0f));

    }
}