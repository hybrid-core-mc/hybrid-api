package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mod.HybridMod;
import hybrid.api.mod.category.ModSettingCategory;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridTheme;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.animation.AlphaAnimation;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModHybridComponent extends HybridComponent {

    private HybridMod hybridMod;
    private final List<ModCategoryComponent> modCategoryComponents = new ArrayList<>();
    AlphaAnimation alphaAnimation;
    int scrollOffset = 0;
    int currentY;
    private int boxWidth, headingHeight;
    public boolean ignoreScroll;



    public void setModComponent(HybridMod mod) {
        this.hybridMod = mod;
        modCategoryComponents.clear();
        assert hybridMod != null;
        for (ModSettingCategory modSettingCategory : mod.getModSettingCategories()) {
            modCategoryComponents.add(new ModCategoryComponent(modSettingCategory));
        }
        alphaAnimation = new AlphaAnimation(0.35f, 0.35f);
        alphaAnimation.setTarget(1f);
    }
    @Override
    public void setupBounds() {

        componentBounds = outerBounds.copy();

        int leftMenuWidth = (int) (outerBounds.getWidth() * HybridTheme.sidebarWidth);

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
        if(alphaAnimation == null) return;
        alphaAnimation.update();
        hybridRenderer.drawQuad(componentBounds,
                HybridThemeMap.get(ThemeColorKey.backgroundColor),
                0,
                HybridTheme.cornerRadius,
                HybridTheme.cornerRadius,
                0);

        if (modCategoryComponents.isEmpty()) {

            HybridRenderText text = HybridTextRenderer.getTextRenderer("No mod settings found!",
                    FontStyle.BOLD,
                    20,
                    Color.WHITE
            );

            float textWidth = text.getWidth();
            float textHeight = text.getHeight();

            int x = (int) (componentBounds.getX() + (componentBounds.getWidth() - textWidth) / 2f);
            int y = (int) (componentBounds.getY() + (componentBounds.getHeight() - textHeight) / 2f);

            text.setPosition(x,y);

            HybridTextRenderer.addText(text);
            return;
        }

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


            bounds.setPosition(getBoxX(), currentModY - scrollOffset);

            bounds.setWidth(boxWidth);

            component.render(renderer, alphaAnimation);


            currentModY += modSpacing + component.getTotalHeight();
        }
    }

    public void drawHeading(HybridRenderer renderer) {

        int boxHeight = 55;

        int x = getBoxX();

        int y = componentBounds.getY() + 17 - scrollOffset;

        ScreenBounds bounds = new ScreenBounds(x,y,boxWidth,boxHeight);
        renderer.drawQuad(bounds, HybridThemeMap.get(ThemeColorKey.modBackgroundColor), HybridTheme.cornerRadius);

        HybridRenderText title = HybridTextRenderer.getTextRenderer(
                hybridMod.getFormattedName(),
                FontStyle.BOLD,
                24,
                alphaAnimation.withAlpha(Color.WHITE),
                true
        );

        String[] descLines = hybridMod
                .getDesc()
                .split("\n");

        int paddingX = HybridTheme.xPadding;
        int lineSpacing = 4;

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
            HybridRenderText descLine = HybridTextRenderer.getTextRenderer(
                    line,
                    FontStyle.REGULAR,
                    16,
                    alphaAnimation.withAlpha(Color.LIGHT_GRAY),
                    alphaAnimation.withAlpha(new Color(255, 255, 255, 1)),
                    false
            );

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
                HybridTextRenderer.getIconRenderer("modrinth",new Color(27, 217, 106)),
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

        renderer.drawOutlineQuad(
                iconsBackground,
                alphaAnimation.withAlpha(HybridThemeMap.get(ThemeColorKey.modsBackgroundColor)),
                alphaAnimation.withAlpha(HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor)),
                10,
                1
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
    public void onCharTyped(CharInput input) {
        modCategoryComponents.forEach(modCategoryComponent -> modCategoryComponent.onCharTyped(input));
        super.onCharTyped(input);
    }

    @Override
    public void keyPressed(KeyInput input) {
        modCategoryComponents.forEach(modCategoryComponent -> modCategoryComponent.keyPressed(input));
        super.keyPressed(input);
    }

    @Override
    public void onMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

        modCategoryComponents.forEach(modCategoryComponent -> modCategoryComponent.onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount));

        if(ignoreScroll) return;

        float scrollSpeed = 20f;

        scrollOffset -= (int) (verticalAmount * scrollSpeed);

        int contentHeight = 0;
        for (ModCategoryComponent c : modCategoryComponents) {
            contentHeight += c.getTotalHeight() + 8;
        }

        int viewportHeight = getViewportHeight();
        int maxScroll = Math.max(0, contentHeight - viewportHeight);

        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        super.onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    private int getViewportHeight() {
        return componentBounds.getHeight() - headingHeight - 20;
    }


    public int getBoxX() {
        return componentBounds.getX() + (componentBounds.getWidth() - boxWidth) / 2;
    }
}
