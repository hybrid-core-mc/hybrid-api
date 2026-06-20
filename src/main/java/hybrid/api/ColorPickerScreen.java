package hybrid.api;

import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import java.awt.*;

public class ColorPickerScreen extends net.minecraft.client.gui.screens.Screen {

    private Color pickedColor = Color.BLACK;
    private Color hoveredColor = Color.BLACK;

    
    private float rotation = 0.0f;

    protected ColorPickerScreen() {
        super(net.minecraft.network.chat.Component.literal("Color Picker [Debug]"));
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

                    
                    int rMix = (int) ((wColor * baseColor.getRed())   + (wWhite * 255) + (wBlack * 0));
                    int gMix = (int) ((wColor * baseColor.getGreen()) + (wWhite * 255) + (wBlack * 0));
                    int bMix = (int) ((wColor * baseColor.getBlue())  + (wWhite * 255) + (wBlack * 0));

                    int alpha = 127; 
                    int finalRgb = (alpha << 24) | (rMix << 16) | (gMix << 8) | bMix;

                    
                    if (guiGraphics != null) {
                        guiGraphics.fill(px, py, px + 1, py + 1, finalRgb);
                    }

                    
                    if (px == mouseX && py == mouseY) {
                        Color sample = new Color(rMix, gMix, bMix);
                        if (processClick) {
                            this.pickedColor = sample;
                        } else {
                            this.hoveredColor = sample;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        float slowAngle = (float) ((System.currentTimeMillis() / 5000.0) % (Math.PI * 2.0));
        rotation = (float) (slowAngle + Math.PI);

        Color activePickerColor = Color.GREEN;

//        drawTriangleAndSample(guiGraphics, new Quad(10, 10, 120, 120), activePickerColor, mouseX, mouseY, false, false);

        HybridRenderer2D.drawTriangle(new Quad(10,10,120,120), activePickerColor);

        guiGraphics.fill(mouseX - 2, mouseY - 2, mouseX + 3, mouseY + 3, Color.WHITE.getRGB());
        guiGraphics.fill(mouseX - 1, mouseY - 1, mouseX + 2, mouseY + 2, this.hoveredColor.getRGB());

        guiGraphics.fill(150, 10, 190, 50, Color.DARK_GRAY.getRGB());
        guiGraphics.fill(152, 12, 188, 48, this.pickedColor.getRGB());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() == 0) {
            int clickX = (int) mouseButtonEvent.x();
            int clickY = (int) mouseButtonEvent.y();

            Color activePickerColor = Color.GREEN;
            drawTriangleAndSample(null, new Quad(10, 10, 120, 120), activePickerColor, clickX, clickY, true, true);
            return true;
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }
}