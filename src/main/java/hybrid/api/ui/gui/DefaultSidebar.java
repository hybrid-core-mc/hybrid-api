package hybrid.api.ui.gui;

import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;

import java.awt.*;

public class DefaultSidebar implements GuiComponents {

    private final Color border = new Color(44, 45, 56);

    @Override
    public void renderSidebar(Quad sidebar, int alignmentX) {
        
        HybridRenderer2D.drawRoundRect(sidebar, 10, 1.5f, border, new Color(22, 25, 35), 0, 0, 10, 10);

        int leftPadding = 24;
        int topPadding = 24;

        int dotSize = 6;
        int logoX = sidebar.x + leftPadding;
        int logoY = sidebar.y + topPadding + 6;
        HybridRenderer2D.drawCircle(new Quad(logoX, logoY - 5, dotSize, dotSize), 3.0f, new Color(99, 102, 241, 255), true);

        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                "HYBRID CORE",
                FontStyle.EXTRABOLD,
                19,
                Color.WHITE,
                new Color(255, 255, 255, 255),
                false
        );

        int textY = sidebar.y + topPadding;
        text.setPosition(alignmentX, textY);
        HybridTextRenderer.addText(text);

        
        int startY = textY + text.getHeight() + 28;
        renderMods(sidebar, startY, alignmentX);

        
        renderSettingsMenu(sidebar);
    }


    private void renderMods(Quad sidebar, int startY, int alignmentX) {
        int boxWidth = (int) (sidebar.getWidth() * 0.78);
        int boxX = sidebar.x + (sidebar.getWidth() - boxWidth) / 2;
        int boxHeight = 28;
        int gap = 6;

        String[] modNames = {"Chat Emoji", "Death Plus", "KillCam", "MCF"};

        for (int i = 0; i < modNames.length; i++) {
            boolean selected = (i == 0);
            int boxY = startY + ((boxHeight + gap) * i);
            Quad modBox = new Quad(boxX, boxY, boxWidth, boxHeight);

            if (selected) {
                HybridRenderer2D.drawRoundRect(
                        modBox, 6, 1f, 
                        new Color(55, 65, 145, 100), 
                        new Color(30, 33, 48, 180), 1
                );
            }

            HybridRenderText modText = HybridTextRenderer.getTextRenderer(
                    modNames[i],
                    FontStyle.BOLD,
                    16,
                    selected ? Color.WHITE : new Color(110, 120, 145),
                    new Color(255, 255, 255, 200),
                    false
            );

            int modTextY = modBox.y + (modBox.getHeight() / 2) - (modText.getHeight() / 2);
            modText.setPosition(alignmentX, modTextY);
            HybridTextRenderer.addText(modText);

            int dotRadius = 4;
            int dotX = (modBox.x + modBox.width) - 20;
            int dotY = modBox.y + (modBox.getHeight() / 2) - (dotRadius / 2);
            Color dotColor = selected ? new Color(16, 185, 129, 255) : new Color(60, 66, 82, 255);

            HybridRenderer2D.drawCircle(new Quad(dotX, dotY, dotRadius, dotRadius), 2.0f, dotColor, selected);
        }
    }

    private void renderSettingsMenu(Quad sidebar) {
        int margin = 14;
        int boxHeight = 33;
        int boxWidth = (int) (sidebar.getWidth() * 0.86);

        int boxX = sidebar.x + (sidebar.getWidth() - boxWidth) / 2;
        int boxY = (sidebar.y + sidebar.getHeight()) - boxHeight - margin;

        Quad settingsBox = new Quad(boxX, boxY, boxWidth, boxHeight);

        HybridRenderer2D.drawRoundRect(settingsBox, 8, 1.0f, new Color(0x1F2126), new Color(0x0D0F14));
    }
}