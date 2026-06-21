package hybrid.api.util.render;

import hybrid.api.mod.settings.ColorSetting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class TriangleGradientPicker {
    int scale;
    private Color pickedColor = Color.BLACK;
    private Color hoveredColor = Color.BLACK;
    private float rotation = 0.0f;
    ColorSetting setting;

    
    private int lastX;
    private int lastY;

    
    private int indicatorX = -1;
    private int indicatorY = -1;

    
    private boolean isDragging = false;

    public TriangleGradientPicker(int scale, ColorSetting colorSetting) {
        this.scale = scale;
        this.setting = colorSetting;
    }

    public void drawTriangleAndSample(GuiGraphics guiGraphics, Quad bounds, Color baseColor, int mouseX, int mouseY, boolean isClick, boolean processClick) {
        int xStr = bounds.getX();
        int yStr = bounds.getY();
        int w = bounds.getWidth();
        int h = bounds.getHeight();

        float centerX = xStr + (w / 2.0f);
        float centerY = yStr + (h / 2.0f);

        float r = 0.22f;
        float k = (float) Math.sqrt(3.0);
        float totalScale = Math.min(w, h) * 1.0f * 1.11f;

        float v1x = (k * r * totalScale);
        float v1y = -(-r * totalScale);

        float v2x = 0;
        float v2y = -(r * 2.0f * totalScale);

        float v3x = (-k * r * totalScale);
        float v3y = -(-r * totalScale);

        float cos = (float) Math.cos(this.rotation);
        float sin = (float) Math.sin(this.rotation);

        float p1x = (v1x * cos - v1y * sin) + centerX;
        float p1y = (v1x * sin + v1y * cos) + centerY;

        float p2x = (v2x * cos - v2y * sin) + centerX;
        float p2y = (v2x * sin + v2y * cos) + centerY;

        float p3x = (v3x * cos - v3y * sin) + centerX;
        float p3y = (v3x * sin + v3y * cos) + centerY;

        int minX = (int) Math.min(p1x, Math.min(p2x, p3x)) - 1;
        int maxX = (int) Math.max(p1x, Math.max(p2x, p3x)) + 1;
        int minY = (int) Math.min(p1y, Math.min(p2y, p3y)) - 1;
        int maxY = (int) Math.max(p1y, Math.max(p2y, p3y)) + 1;

        if (!isClick) {
            this.hoveredColor = Color.BLACK;
        }

        float denom = (p2y - p3y) * (p1x - p3x) + (p3x - p2x) * (p1y - p3y);
        if (Math.abs(denom) < 0.0001f) return;

        for (int px = minX; px <= maxX; px++) {
            for (int py = minY; py <= maxY; py++) {

                boolean b1 = ((px - p2x) * (p1y - p2y) - (p1x - p2x) * (py - p2y)) < 0.0f;
                boolean b2 = ((px - p3x) * (p2y - p3y) - (p2x - p3x) * (py - p3y)) < 0.0f;
                boolean b3 = ((px - p1x) * (p3y - p1y) - (p3x - p1x) * (py - p1y)) < 0.0f;

                if ((b1 == b2) && (b2 == b3)) {

                    float wWhite = ((p2y - p3y) * (px - p3x) + (p3x - p2x) * (py - p3y)) / denom;
                    float wColor = ((p3y - p1y) * (px - p3x) + (p1x - p3x) * (py - p3y)) / denom;
                    float wBlack = 1.0f - wColor - wWhite;

                    wColor = Math.max(0.0f, Math.min(1.0f, wColor));
                    wWhite = Math.max(0.0f, Math.min(1.0f, wWhite));
                    wBlack = Math.max(0.0f, Math.min(1.0f, wBlack));

                    int rMix = (int) ((wColor * baseColor.getRed()) + (wWhite * 255) + (wBlack * 0));
                    int gMix = (int) ((wColor * baseColor.getGreen()) + (wWhite * 255) + (wBlack * 0));
                    int bMix = (int) ((wColor * baseColor.getBlue()) + (wWhite * 255) + (wBlack * 0));

                    int alpha = 255;
                    int finalRgb = (alpha << 24) | (rMix << 16) | (gMix << 8) | bMix;

                    if (guiGraphics != null) {
                        guiGraphics.fill(px, py, px + 1, py + 1, finalRgb);
                    }

                    
                    if (px == mouseX && py == mouseY) {
                        Color sample = new Color(rMix, gMix, bMix);

                        if (processClick) {
                            
                            int currentAlpha = setting.get().getAlpha();
                            setting.set(new Color(sample.getRed(), sample.getGreen(), sample.getBlue(), currentAlpha));
                            this.pickedColor = sample;

                            
                            this.indicatorX = px;
                            this.indicatorY = py;
                        } else {
                            this.hoveredColor = sample;
                        }
                    }
                }
            }
        }
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y, float angle) {
        
        this.lastX = x;
        this.lastY = y;
        this.rotation = (float) (angle + Math.PI);

        
        
        Color currentSettingColor = setting.get() != null ? setting.get() : Color.GREEN;
        float[] hsb = Color.RGBtoHSB(currentSettingColor.getRed(), currentSettingColor.getGreen(), currentSettingColor.getBlue(), null);

        
        Color pureHueColor = Color.getHSBColor(hsb[0], 1.0f, 1.0f);

        
        if (isDragging) {
            drawTriangleAndSample(guiGraphics, new Quad(x, y, scale, scale), pureHueColor, mouseX, mouseY, true, true);
        } else {
            drawTriangleAndSample(guiGraphics, new Quad(x, y, scale, scale), pureHueColor, mouseX, mouseY, false, false);
        }

        
        if (indicatorX == -1 || indicatorY == -1) {
            indicatorX = x + (scale / 2);
            indicatorY = y + (scale / 2);
        }

        
        guiGraphics.fill(indicatorX - 2, indicatorY - 2, indicatorX + 3, indicatorY + 3, Color.WHITE.getRGB());
        guiGraphics.fill(indicatorX - 1, indicatorY - 1, indicatorX + 2, indicatorY + 2, this.pickedColor.getRGB());
    }

    public void mouseClicked(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button() == 0) {
            int clickX = (int) mouseButtonEvent.x();
            int clickY = (int) mouseButtonEvent.y();

            if (clickX >= lastX && clickX <= lastX + scale && clickY >= lastY && clickY <= lastY + scale) {
                isDragging = true;

                Color currentSettingColor = setting.get() != null ? setting.get() : Color.GREEN;
                float[] hsb = Color.RGBtoHSB(currentSettingColor.getRed(), currentSettingColor.getGreen(), currentSettingColor.getBlue(), null);
                Color pureHueColor = Color.getHSBColor(hsb[0], 1.0f, 1.0f);

                drawTriangleAndSample(null, new Quad(lastX, lastY, scale, scale), pureHueColor, clickX, clickY, true, true);
            }
        }
    }

    public void mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            isDragging = false;
        }
    }

    public void mouseDragged(MouseButtonEvent mouseButtonEvent) {

    }
}