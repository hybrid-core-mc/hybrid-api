package hybrid.api.ui.gui.normal;

import hybrid.api.mod.HybridMod;
import hybrid.api.mod.settings.BuiltCategory;
import hybrid.api.ui.gui.parts.ContentPart;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultSettingsPage implements ContentPart {
    private static final Color BORDER_COLOR = new Color(35, 36, 45);
    private static final Color BG_COLOR = new Color(19, 21, 29, 255);
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color DESC_IDLE_COLOR = new Color(150, 160, 175);
    private static final Color DESC_SHADOW_COLOR = new Color(255, 255, 255, 180);
    private final HybridMod mod;
    private final List<HybridRenderText> cachedLineTexts = new ArrayList<>();

    // NEW: Persistent list caching the category blocks and their bounds
    private final List<DefaultCategoryBlock> categoryBlocks = new ArrayList<>();
    public void mouseClick(MouseButtonEvent event) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();

        for (DefaultCategoryBlock block : categoryBlocks) {
            if (block.isHovered(mouseX, mouseY)) {
                // Forward the event execution straight into the category block instance
                block.mouseClick(event);
                break; // Stop checking other categories once the target is found
            }
        }
    }

    public void mouseRelease(MouseButtonEvent event) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();

        for (DefaultCategoryBlock block : categoryBlocks) {
            if (block.isHovered(mouseX, mouseY)) {
                block.mouseRelease(event);
                break;
            }
        }
    }
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

        Quad links = centerBox.copy().setHeight(25).setWidth(100).setY(cachedTitleText.getY()).setX(centerBox.x + centerBox.getWidth() - 120);
        HybridRenderer2D.drawRoundRect(links, 8, 1.5f, new Color(0x1F2126), new Color(0x0D0F14));

        HybridRenderText linkText = HybridTextRenderer.getTextRenderer(
                "GitHub", FontStyle.REGULAR, 14, new Color(150, 160, 175), new Color(255, 255, 255, 180), false
        );

        int linkTextY = links.getY() + (links.getHeight() / 2) - (linkText.getHeight() / 2);
        linkText.setPosition(links.getX() + 12, linkTextY);
        HybridTextRenderer.addText(linkText);

        // --- CHANGED: Categories are now rendered via the cached blocks ---
        int currentY = centerBox.getHeight() + 150;

        for (DefaultCategoryBlock block : categoryBlocks) {
            Quad categoryQuad = new Quad(
                    boxX,
                    currentY,
                    boxWidth,
                    block.getHeight()
            );

            // Update or assign the dimensions to the block so it knows where it is rendered
            block.render(categoryQuad);

            currentY += block.getHeight() + 150;
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

        // NEW: Rebuild the category blocks cache when the layout invalidates
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

    /**
     * NEW: Getter to access the instantiated blocks for event processing.
     */
    public List<DefaultCategoryBlock> getCategoryBlocks() {
        return this.categoryBlocks;
    }
}