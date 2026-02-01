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
import net.minecraft.client.gui.Click;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModHybridComponent extends HybridComponent {

    private final HybridMod hybridMod;
    private final List<ModCategoryComponent> modCategoryComponents = new ArrayList<>();

    int scrollOffset = 0;
    int currentY;
    private int boxWidth, headingHeight;

    public ModHybridComponent(HybridMod hybridMod, int windowHeight) {
        this.hybridMod = hybridMod;
        for (ModSettingCategory modSettingCategory : hybridMod.getModSettingCategories()) {
            modCategoryComponents.add(new ModCategoryComponent(modSettingCategory));
        }
        System.out.println("init the componenet category");
    }

    @Override
    public void setupBounds() {

        componentBounds = outerBounds.copy();

        int leftMenuWidth = (int) (outerBounds.getWidth() * Theme.sidebarWidth);

        currentY = outerBounds.getY();

        componentBounds.setX(outerBounds.getX() + leftMenuWidth);
        componentBounds.setWidth(outerBounds.getWidth() - leftMenuWidth);

        boxWidth = (int) (componentBounds.getWidth() * 0.9);

        for (ModCategoryComponent component : modCategoryComponents) {
            component.outerBounds = componentBounds.copy();
            component.setupBounds();
        }

        modCategoryComponents.forEach(ModCategoryComponent::setupBounds);

        super.setupBounds();
    }




    @Override
    public void render(HybridRenderer hybridRenderer) {

        hybridRenderer.drawQuad(componentBounds, Theme.backgroundColor);

        ScreenBounds corner = new ScreenBounds(componentBounds.getX(), componentBounds.getY(), Theme.cornerRadius, componentBounds.getHeight());

        hybridRenderer.drawQuad(corner, Theme.backgroundColor, 0);

        drawHeading(hybridRenderer);
        drawSettings(hybridRenderer);

        ScreenBounds debug = componentBounds.copy();
        debug.setHeight(modCategoryComponents
                .getFirst()
                .getTotalHeight() + headingHeight);
    }


    public void drawSettings(HybridRenderer renderer) {


        int modSpacing = 10;
        int currentModY = headingHeight + modSpacing + outerBounds.getY();

        for (ModCategoryComponent component : modCategoryComponents) {

            ScreenBounds bounds = component.componentBounds;


            bounds.setPosition(getBoxX(), currentModY + scrollOffset); // uh  currentModY - scrollOffset

            bounds.setWidth(boxWidth);

            component.render(renderer);
            currentModY += modSpacing;
        }
    }

    public void drawHeading(HybridRenderer renderer) {

        int boxHeight = 55;

        int x = getBoxX();

        int y = componentBounds.getY() + 17 + scrollOffset;

        renderer.drawQuad(new ScreenBounds(x, y, boxWidth, boxHeight), Theme.modBackgroundColor);

        HybridRenderText title = HybridTextRenderer.getTextRenderer(hybridMod.getName(), FontStyle.BOLD, 24, Color.WHITE, true);

        String[] descLines = hybridMod
                .getDesc()
                .split("\n");

        int paddingX = Theme.xPadding;
        int lineSpacing = 4; // no need since this will alwas be the same

        int descLineHeight = HybridTextRenderer
                .getTextRenderer("A", FontStyle.REGULAR, 14, Color.LIGHT_GRAY, false)
                .getHeight();

        int totalDescHeight = descLines.length * descLineHeight + (descLines.length - 1) * lineSpacing;

        int totalTextHeight = title.getHeight() + lineSpacing + totalDescHeight;

        int startY = y + (boxHeight - totalTextHeight) / 2;

        title.setPosition(x + paddingX, startY);
        HybridTextRenderer.addText(title);

        int currentY = startY + title.getHeight() + lineSpacing;

        for (String line : descLines) {
            HybridRenderText descLine = HybridTextRenderer.getTextRenderer(line, FontStyle.REGULAR, 16, Color.LIGHT_GRAY,new Color(255, 255, 255, 1), true);

            descLine.setPosition(x + paddingX, currentY);
            HybridTextRenderer.addText(descLine);

            currentY += descLineHeight + lineSpacing;
        }

        drawIconGrid(renderer, x, y, boxWidth, boxHeight, paddingX);
        headingHeight = boxHeight + 17;
    }


    private void drawIconGrid(HybridRenderer renderer,
                              int headingX,
                              int headingY,
                              int headingWidth,
                              int headingHeight,
                              int paddingX) {

        HybridRenderText[] icons = {
                HybridTextRenderer.getIconRenderer("github", Color.WHITE),
                HybridTextRenderer.getIconRenderer("modrinth", new Color(27, 217, 106)),
                HybridTextRenderer.getIconRenderer("star", Color.WHITE),
                HybridTextRenderer.getIconRenderer("reset", Color.WHITE)
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

    @Override
    public void onMouseClicked(Click click) {
        modCategoryComponents.forEach(modCategoryComponent -> modCategoryComponent.onMouseClicked(click));
        super.onMouseClicked(click);
    }

    @Override
    public void onMouseRelease(Click click) {
        modCategoryComponents.forEach(modCategoryComponent -> modCategoryComponent.onMouseRelease(click));
        super.onMouseRelease(click);
    }

    @Override
    public void onMouseDrag(Click click) {
        modCategoryComponents.forEach(modCategoryComponent -> modCategoryComponent.onMouseDrag(click));
        super.onMouseDrag(click);
    }

    @Override
    public void onMouseScroll(double mouseX, double mouseY,
                              double horizontalAmount, double verticalAmount) {

        float scrollSpeed = 5f;

        scrollOffset += (int) (verticalAmount * scrollSpeed);

        super.onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
    }


    public int getBoxX() {
        return componentBounds.getX() + (componentBounds.getWidth() - boxWidth) / 2;
    }
}
