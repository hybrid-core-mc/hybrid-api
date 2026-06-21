package hybrid.api.ui.gui.normal;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import hybrid.api.ui.gui.parts.SidebarPart;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class DefaultSidebar extends SidebarPart {

    private final Color border = new Color(44, 45, 56);

    
    private final float FADE_SPEED = 0.08f; 

    
    private final float[] modHoverProgress = new float[4]; 
    private final float[] settingsHoverProgress = new float[3]; 

    
    private long lastFrameTime = System.currentTimeMillis();

    @Override
    public void renderSidebar(Quad bounds, int alignmentX, int mouseX, int mouseY) {
        
        long currentTime = System.currentTimeMillis();
        float delta = (currentTime - lastFrameTime) / 16.66f; 
        lastFrameTime = currentTime;
        if (delta > 3) delta = 3; 

        HybridRenderer2D.drawRoundRect(bounds, new Color(22, 25, 35), border, 10, 1.5f, 0, 0, 10, 10);

        int leftPadding = 24;
        int topPadding = 24;

        int dotSize = 6;
        int offzet = 0;
        int logoX = bounds.x + leftPadding;
        int logoY = bounds.y + topPadding + 6;
        HybridRenderer2D.drawCircle(new Quad(logoX-offzet, logoY - 5, dotSize, dotSize), 3.0f, new Color(99, 102, 241, 255), true);

        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                "HYBRID CORE",
                FontStyle.EXTRABOLD,
                19,
                Color.WHITE,
                new Color(255, 255, 255, 255),
                false
        );

        int textY = bounds.y + topPadding;
        text.setPosition(alignmentX-offzet, textY);
        HybridTextRenderer.addText(text);

        int startY = textY + text.getHeight() + 28;
        renderMods(bounds, startY, alignmentX, mouseX, mouseY, delta);

        renderSettingsMenu(bounds, mouseX, mouseY, delta);
    }


    private void renderMods(Quad sidebar, int startY, int alignmentX, int mouseX, int mouseY, float delta) {
        int boxWidth = (int) (sidebar.getWidth() * 0.78);
        int boxX = sidebar.x + (sidebar.getWidth() - boxWidth) / 2;
        int boxHeight = 26;
        int gap = 6;

        String[] modNames = {"Chat Emoji", "Death Plus", "KillCam", "MCF"};

        for (int i = 0; i < modNames.length; i++) {
            boolean selected = (i == 0);
            int boxY = startY + ((boxHeight + gap) * i);
            Quad modBox = new Quad(boxX, boxY, boxWidth, boxHeight);

            boolean hovered = isHovered(modBox, mouseX, mouseY);

            
            if (hovered) {
                modHoverProgress[i] = Math.min(1.0f, modHoverProgress[i] + (FADE_SPEED * delta));
                RenderContext.get().requestCursor(CursorTypes.POINTING_HAND);
            } else {
                modHoverProgress[i] = Math.max(0.0f, modHoverProgress[i] - (FADE_SPEED * delta));
            }

            float progress = modHoverProgress[i];

            if (selected) {
                HybridRenderer2D.drawRoundRect(modBox, new Color(30, 33, 48, 180), new Color(55, 65, 145, 100), 6, 1f,
                        1
                );
            } else if (progress > 0) {
                
                int fillAlpha = (int) (0x1F * progress);
                int borderAlpha = (int) (0x3A * progress);
                Color animatedFill = new Color(0x1F, 0x22, 0x2C, fillAlpha);
                Color animatedBorder = new Color(0x2C, 0x2F, 0x3A, borderAlpha);

                HybridRenderer2D.drawRoundRect(modBox, animatedBorder, animatedFill, 6, 0, 0);
            }

            
            Color textColor;
            if (selected) {
                textColor = Color.WHITE;
            } else if (hovered) {
                textColor = new Color(180, 190, 220);
            } else {
                textColor = new Color(110, 120, 145);
            }

            HybridRenderText modText = HybridTextRenderer.getTextRenderer(
                    modNames[i],
                    FontStyle.BOLD,
                    16, textColor,
                    new Color(255, 255, 255, 200),
                    false
            );

            int modTextY = modBox.y + (modBox.getHeight() / 2) - (modText.getHeight() / 2);
            modText.setPosition(alignmentX-13, modTextY);
            HybridTextRenderer.addText(modText);

            int dotRadius = 4;
            int dotX = (modBox.x + modBox.width) - 20;
            int dotY = modBox.y + (modBox.getHeight() / 2) - (dotRadius / 2);

            
            Color dotColor;
            if (selected) {
                dotColor = new Color(16, 185, 129, 255);
            } else if (hovered) {
                dotColor = new Color(100, 110, 130, 255);
            } else {
                dotColor = new Color(60, 66, 82, 255);
            }

            HybridRenderer2D.drawCircle(new Quad(dotX, dotY, dotRadius, dotRadius), 2.0f, dotColor, selected);
        }
    }

    private void renderSettingsMenu(Quad sidebar, int mouseX, int mouseY, float delta) {
        int margin = 14;
        int boxHeight = 27;
        int boxWidth = (int) (sidebar.getWidth() * 0.80);

        int boxX = sidebar.x + (sidebar.getWidth() - boxWidth) / 2;
        int boxY = (sidebar.y + sidebar.getHeight()) - boxHeight - margin;

        Quad settingsBox = new Quad(boxX, boxY, boxWidth, boxHeight);
        HybridRenderer2D.drawRoundRect(settingsBox, new Color(0x0D0F14), new Color(0x1F2126), 8, 1.0f);

        int padding = 10;
        int bgSize = 20;

        var menuIcons = new Object[][]{{"expand", boxX + padding}, {"theme", boxX + (boxWidth / 2) - (bgSize / 2)}, {"setting", boxX + boxWidth - padding - bgSize}};

        int bgY = boxY + (boxHeight - bgSize) / 2;

        for (int i = 0; i < menuIcons.length; i++) {
            Object[] iconData = menuIcons[i];
            String iconName = (String) iconData[0];
            int bgX = (int) iconData[1];

            HybridRenderText icon = HybridTextRenderer.getIconRenderer(iconName, Color.WHITE);
            Quad bgQuad = new Quad(bgX, bgY, bgSize, bgSize);

            boolean hovered = isHovered(bgQuad, mouseX, mouseY);

            if (hovered) {
                settingsHoverProgress[i] = Math.min(1.0f, settingsHoverProgress[i] + (FADE_SPEED * delta));
                RenderContext.get().requestCursor(CursorTypes.POINTING_HAND);
            } else {
                settingsHoverProgress[i] = Math.max(0.0f, settingsHoverProgress[i] - (FADE_SPEED * delta));
            }

            float progress = settingsHoverProgress[i];

            if (progress > 0) {
                int alpha = (int) (150 * progress);
                Color animatedBgBorderColor = new Color(40, 42, 54, alpha);
                HybridRenderer2D.drawRoundRect(bgQuad, animatedBgBorderColor, animatedBgBorderColor, 4, 0);
            }

            icon.setPosition(bgX + (bgSize - icon.getWidth()) / 2, bgY + (bgSize - icon.getHeight()) / 2);
            HybridTextRenderer.addText(icon);
        }
    }

    private boolean isHovered(Quad box, int mouseX, int mouseY) {
        return mouseX >= box.x && mouseX <= (box.x + box.width) && mouseY >= box.y && mouseY <= (box.y + box.height);
    }

    @Override
    public void mouseReleased(MouseButtonEvent mouseButtonEvent) {
        super.mouseReleased(mouseButtonEvent);
    }
}