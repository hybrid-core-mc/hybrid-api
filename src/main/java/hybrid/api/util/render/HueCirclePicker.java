package hybrid.api.util.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class HueCirclePicker {

    private final int radius;

    public float selectedHue = 0.0f;
    public Color selectedColor = Color.GREEN;

    public float centerX;
    public float centerY;

    public HueCirclePicker(int radius) {
        this.radius = radius;
    }

    public void draw(float cx, float cy, Color ignored) {

        this.centerX = cx;
        this.centerY = cy;



        GuiGraphics ctx = RenderContext.get();

        int radiuz = radius - 11;
        float innerRadius = radiuz * 0.88f;

        for (int y = -radiuz; y <= radiuz; y++) {
            for (int x = -radiuz; x <= radiuz; x++) {

                float distSq = x * x + y * y;

                if (distSq > radiuz * radiuz) continue;
                if (distSq < innerRadius * innerRadius) continue;

                float angle = (float) Math.atan2(y, x);

                float hue = angle / ((float)(Math.PI * 2.0));
                hue += 0.25f;
                hue -= (float) Math.floor(hue);

                Color color = Color.getHSBColor(hue, 1.0f, 1.0f);

                int px = Math.round(cx + x);
                int py = Math.round(cy + y);

                ctx.fill(px, py, px + 1, py + 1, color.getRGB());
            }
        }

        Color preview = selectedColor;

        int boxSize = 20;
        int bx = (int) cx + radius + 10;
        int by = (int) cy - boxSize / 2;

        ctx.fill(bx, by, bx + boxSize, by + boxSize, preview.getRGB());

        int normalized = radius;
        HybridRenderer2D.drawHueCircle(
                new Quad((int) cx-normalized, (int) cy-normalized , 0, 0),
                radius,
                preview
        );
    }
    public void mouseClicked(MouseButtonEvent event) {

        float mouseX = (float) event.x();
        float mouseY = (float) event.y();

        int radiuz = radius - 11;

        float dx = mouseX - centerX;
        float dy = mouseY - centerY;

        float distSq = dx * dx + dy * dy;

        float outer = radiuz * radiuz;

        if (distSq > outer) {
            return;
        }

        float angle = (float) Math.atan2(dy, dx);

        float hue = angle / ((float)(Math.PI * 2.0));
        hue += 0.25f;
        hue -= (float) Math.floor(hue);

        selectedHue = hue;
        selectedColor = Color.getHSBColor(selectedHue, 1.0f, 1.0f);
    }
}