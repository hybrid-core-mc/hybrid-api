package hybrid.api.util.render;

import hybrid.api.mod.settings.ColorSetting;
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

    public void mouseDragged(MouseButtonEvent event) {
        float mouseX = (float) event.x();
        float mouseY = (float) event.y();

        float dx = mouseX - centerX;
        float dy = mouseY - centerY;
        float distSq = dx * dx + dy * dy;

        
        int innerRadiuz = radius - 11; 
        float innerMin = (float) (innerRadiuz * innerRadiuz);

        
        int lenientOuterRadius = radius + 10;
        float outerMax = (float) (lenientOuterRadius * lenientOuterRadius);

        
        if (distSq < innerMin) {
            return;
        }

        
        if (distSq > outerMax) {
            return;
        }

        
        float angle = (float) Math.atan2(dy, dx);

        float hue = angle / ((float)(Math.PI * 2.0));
        hue += 0.25f;
        hue -= (float) Math.floor(hue);

        selectedHue = hue;

        
        int currentAlpha = colorSetting.get().getAlpha();
        Color hsbColor = Color.getHSBColor(selectedHue, 1.0f, 1.0f);

        colorSetting.set(new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), currentAlpha));
    }

    public void mouseClicked(MouseButtonEvent event) {

    }

    public void mouseReleased(MouseButtonEvent event) {

    }
}