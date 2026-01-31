package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.components.HybridComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModHybridComponent extends HybridComponent {

    private final HybridMod hybridMod;
    private final List<ModCategoryComponent> modCategoryComponents = new ArrayList<>();

    private int boxWidth;

    public ModHybridComponent(HybridMod hybridMod) {
        this.hybridMod = hybridMod;
    }

    @Override
    public void setupBounds() {
        componentBounds = outerBounds.copy();

        int leftMenuWidth = (int) (outerBounds.getWidth() * Theme.sidebarWidth);

        componentBounds.setX(outerBounds.getX() + leftMenuWidth);
        componentBounds.setWidth(outerBounds.getWidth() - leftMenuWidth);

        boxWidth = (int) (componentBounds.getWidth() * 0.9);

        layoutCategories();

        modCategoryComponents.forEach(ModCategoryComponent::setupBounds);

        super.setupBounds();
    }


    private void layoutCategories() {
        modCategoryComponents.clear();

        int headingHeight = 55;
        int headingMarginTop = 17;
        int spacing = 8;

        int currentY = componentBounds.getY() + headingMarginTop + headingHeight + 20;

        int categoryHeight = 70;

        for (ModSettingCategory category : hybridMod.getModSettingCategories()) {

            ModCategoryComponent component = new ModCategoryComponent(category);

            component.outerBounds = (new ScreenBounds(componentBounds.getX(), currentY, boxWidth, categoryHeight));

            component.setupBounds();

            modCategoryComponents.add(component);

            currentY += categoryHeight + spacing;
        }
    }


    @Override
    public void render(HybridRenderer hybridRenderer) {

        hybridRenderer.drawQuad(componentBounds, Theme.backgroundColor);

        ScreenBounds corner = new ScreenBounds(componentBounds.getX(), componentBounds.getY(), Theme.cornerRadius, componentBounds.getHeight());

        hybridRenderer.drawQuad(corner, Theme.backgroundColor, 0);

        drawHeading(hybridRenderer);
        drawSettings(hybridRenderer);
    }


    public void drawSettings(HybridRenderer renderer) {

        int headingX = componentBounds.getX() + (componentBounds.getWidth() - boxWidth) / 2;

        for (ModCategoryComponent component : modCategoryComponents) {

            ScreenBounds bounds = component.componentBounds;

            bounds.setX(headingX);
            bounds.setWidth(boxWidth);

            component.setupBounds();
            component.render(renderer);
        }
    }

    public void drawHeading(HybridRenderer renderer) {

        int boxHeight = 55;

        int x = componentBounds.getX() + (componentBounds.getWidth() - boxWidth) / 2;

        int y = componentBounds.getY() + 17;

        renderer.drawQuad(new ScreenBounds(x, y, boxWidth, boxHeight), Theme.modBackgroundColor);

        HybridRenderText title = HybridTextRenderer.getTextRenderer(hybridMod.getName(), FontStyle.BOLD, 24, Color.WHITE, true);

        String[] descLines = hybridMod
                .getDesc()
                .split("\n");

        int paddingX = 12;
        int spacing = 4;

        int descLineHeight = HybridTextRenderer
                .getTextRenderer("A", FontStyle.REGULAR, 14, Color.LIGHT_GRAY, true)
                .getHeight();

        int totalDescHeight = descLines.length * descLineHeight + (descLines.length - 1) * spacing;

        int totalTextHeight = title.getHeight() + spacing + totalDescHeight;

        int startY = y + (boxHeight - totalTextHeight) / 2;

        title.setPosition(x + paddingX, startY);
        HybridTextRenderer.addText(title);

        int currentY = startY + title.getHeight() + spacing;

        for (String line : descLines) {
            HybridRenderText descLine = HybridTextRenderer.getTextRenderer(line, FontStyle.REGULAR, 16, Color.LIGHT_GRAY, true);

            descLine.setPosition(x + paddingX, currentY);
            HybridTextRenderer.addText(descLine);

            currentY += descLineHeight + spacing;
        }

        drawIconGrid(renderer, x, y, boxWidth, boxHeight, paddingX);
    }

    // -------------------------------------------------
    // Icons
    // -------------------------------------------------
    private void drawIconGrid(HybridRenderer renderer,
                              int headingX,
                              int headingY,
                              int headingWidth,
                              int headingHeight,
                              int paddingX) {

        HybridRenderText[] icons = {
                HybridTextRenderer.getIconRenderer("github", 0, 0, Color.WHITE),
                HybridTextRenderer.getIconRenderer("modrinth", 0, 0, new Color(27, 217, 106)),
                HybridTextRenderer.getIconRenderer("star", 0, 0, Color.WHITE),
                HybridTextRenderer.getIconRenderer("reset", 0, 0, Color.WHITE)
        };

        int iconBoxSize = 20;
        int iconPadding = 1;
        int bgMargin = 2;

        int gridWidth = iconBoxSize * 2 + iconPadding;
        int gridHeight = iconBoxSize * 2 + iconPadding;

        int iconGridX = headingX + headingWidth - gridWidth - paddingX;

        int iconGridY = headingY + (headingHeight - gridHeight) / 2;

        ScreenBounds iconsBackground = new ScreenBounds(
                iconGridX - bgMargin,
                iconGridY - bgMargin,
                gridWidth + bgMargin * 2,
                gridHeight + bgMargin * 2
        );

        renderer.drawOutlineQuad(iconsBackground, Theme.modsBackgroundColor, Theme.modButtonOutlineColor, 10, 1
        );

        for (int i = 0; i < icons.length; i++) {
            HybridRenderText icon = icons[i];

            int col = i % 2;
            int row = i / 2;

            int iconX =
                    iconGridX
                            + col * (iconBoxSize + iconPadding)
                            + (iconBoxSize - icon.getWidth()) / 2;

            int iconY =
                    iconGridY
                            + row * (iconBoxSize + iconPadding)
                            + (iconBoxSize - icon.getHeight()) / 2;

            icon.setPosition(iconX, iconY);
            HybridTextRenderer.addText(icon);
        }
    }
}
