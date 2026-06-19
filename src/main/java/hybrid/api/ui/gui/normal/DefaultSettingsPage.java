package hybrid.api.ui.gui.normal;

import hybrid.api.mod.HybridMod;
import hybrid.api.mod.settings.BuiltCategory;
import hybrid.api.ui.gui.category.DefaultCategoryBlock;
import hybrid.api.ui.gui.parts.ContentPart;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultSettingsPage extends ContentPart {
    private static final Color BORDER_COLOR = new Color(35, 36, 45);
    private static final Color BG_COLOR = new Color(19, 21, 29, 255);
    private static final Color TITLE_COLOR = Color.WHITE;
    public static final Color DESC_IDLE_COLOR = new Color(150, 160, 175);
    public static final Color DESC_SHADOW_COLOR = new Color(255, 255, 255, 180);

    private final HybridMod mod;
    private final List<HybridRenderText> cachedLineTexts = new ArrayList<>();
    private final List<DefaultCategoryBlock> categoryBlocks = new ArrayList<>();

    
    private HybridRenderText iconSettings;
    private HybridRenderText iconTheme;
    private HybridRenderText iconExpand;

    private String cachedDescription;
    private int cachedBoxWidth = -1;
    private int cachedBoxX = -1;
    private HybridRenderText cachedTitleText;
    private int finalCalculatedHeight = 0;

    public DefaultSettingsPage(HybridMod mod) {
        this.mod = mod;
    }

    public void render(Quad quad) {
        int boxWidth = (int) (quad.getWidth() * 0.93);
        int boxX = quad.getX() + (quad.getWidth() - boxWidth) / 2;
        int boxY = quad.y + 20;

        String currentDesc = mod.getDescription();
        if (cachedBoxWidth != boxWidth || cachedBoxX != boxX || !currentDesc.equals(cachedDescription)) {
            rebuildLayoutCache(currentDesc, boxX, boxY, boxWidth);
        }

        Quad centerBox = new Quad(boxX, boxY, boxWidth, finalCalculatedHeight);
        HybridRenderer2D.drawRoundRect(centerBox, 10, 1.5f, BORDER_COLOR, BG_COLOR);

        HybridTextRenderer.addText(cachedTitleText);

        for (HybridRenderText cachedLineText : cachedLineTexts) {
            HybridTextRenderer.addText(cachedLineText);
        }

        Quad links = centerBox.copy().setY(cachedTitleText.getY()).setX(centerBox.x + centerBox.getWidth() - 100);

        
        positionSettingsMenu(links);

        
        HybridTextRenderer.addText(iconSettings);
        HybridTextRenderer.addText(iconTheme);
        HybridTextRenderer.addText(iconExpand);

        int spacing = 132;
        int categoryPadding  = 15;
        int currentY = centerBox.getHeight() + spacing;

        for (DefaultCategoryBlock block : categoryBlocks) {
            Quad categoryQuad = new Quad(
                    boxX,
                    currentY,
                    boxWidth,
                    block.getHeight()
            );

            block.render(categoryQuad);
            currentY += block.getHeight() + categoryPadding;
        }
    }

    private void positionSettingsMenu(Quad sidebar) {
        int boxHeight = 25;
        int boxWidth = 85;

        int boxX = sidebar.x;
        int boxY = sidebar.y + 2;

        Quad settingsBox = new Quad(boxX, boxY, boxWidth, boxHeight);
        HybridRenderer2D.drawRoundRect(settingsBox, 8, 1.0f, new Color(0x1F2126), new Color(0x0D0F14));

        int padding = 10;

        if (iconSettings != null && iconTheme != null && iconExpand != null) {
            iconSettings.setPosition(boxX + padding, boxY + (boxHeight - iconSettings.getHeight()) / 2);
            iconTheme.setPosition(boxX + (boxWidth / 2) - (iconTheme.getWidth() / 2), boxY + (boxHeight - iconTheme.getHeight()) / 2);
            iconExpand.setPosition(boxX + boxWidth - padding - iconExpand.getWidth(), boxY + (boxHeight - iconExpand.getHeight()) / 2);
        }
    }

    private void rebuildLayoutCache(String description, int boxX, int boxY, int boxWidth) {
        this.cachedDescription = description;
        this.cachedBoxWidth = boxWidth;
        this.cachedBoxX = boxX;

        this.cachedLineTexts.clear();

        int leftPadding = 18;
        int titleDescGap = 6;
        int lineGap = 3;
        int textX = boxX + leftPadding;

        int currentTrackedHeight = 10;

        this.cachedTitleText = HybridTextRenderer.getTextRenderer(
                mod.getName(), FontStyle.BOLD, 23, TITLE_COLOR, TITLE_COLOR, false
        );
        this.cachedTitleText.setPosition(textX, boxY + currentTrackedHeight);

        currentTrackedHeight += this.cachedTitleText.getHeight() + titleDescGap;

        List<String> lines = wrapByWordCount(description);

        for (String line : lines) {
            HybridRenderText lineText = HybridTextRenderer.getTextRenderer(
                    line, FontStyle.REGULAR, 14, DESC_IDLE_COLOR, DESC_SHADOW_COLOR, false
            );

            lineText.setPosition(textX, boxY + currentTrackedHeight);
            this.cachedLineTexts.add(lineText);

            currentTrackedHeight += lineText.getHeight() + lineGap;
        }

        if (!lines.isEmpty()) {
            currentTrackedHeight -= lineGap;
        }

        currentTrackedHeight += 10;
        this.finalCalculatedHeight = currentTrackedHeight;

        
        this.iconSettings = HybridTextRenderer.getIconRenderer("github", Color.WHITE);
        this.iconTheme = HybridTextRenderer.getIconRenderer("modrinth", new Color(25, 194, 97));
        this.iconExpand = HybridTextRenderer.getIconRenderer("star", Color.WHITE);

        this.categoryBlocks.clear();
        for (BuiltCategory category : mod.getCategories()) {
            this.categoryBlocks.add(new DefaultCategoryBlock(category));
        }
    }

    private List<String> wrapByWordCount(String text) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) return lines;

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder(32);
        int wordCount = 0;

        for (String word : words) {
            if (word.isEmpty()) continue;

            if (wordCount >= 10) {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
                currentLine.append(word);
                wordCount = 1;
            } else {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
                wordCount++;
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    @Override
    public void mouseReleased(MouseButtonEvent mouseButtonEvent) {
        for (DefaultCategoryBlock block : categoryBlocks) {
            block.mouseReleased(mouseButtonEvent);
        }
        super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public void mouseClicked(MouseButtonEvent mouseButtonEvent) {
        for (DefaultCategoryBlock block : categoryBlocks) {
            block.mouseClicked(mouseButtonEvent);
        }
        super.mouseClicked(mouseButtonEvent);
    }

    @Override
    public void mouseDragged(MouseButtonEvent mouseButtonEvent) {
        for (DefaultCategoryBlock block : categoryBlocks) {
            block.mouseDragged(mouseButtonEvent);
        }
        super.mouseDragged(mouseButtonEvent);
    }

    @Override
    public void mouseScrolled(double d, double e, double f, double g) {
        for (DefaultCategoryBlock block : categoryBlocks) {
            block.mouseScrolled(d, e, f, g);
        }
        super.mouseScrolled(d, e, f, g);
    }
}