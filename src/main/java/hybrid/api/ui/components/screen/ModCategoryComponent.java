package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.mods.settings.ModSetting;
import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.components.HybridComponent;
import hybrid.api.ui.components.settings.BooleanComponent;
import hybrid.api.ui.components.settings.NumberComponent;
import net.minecraft.client.gui.Click;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModCategoryComponent extends HybridComponent {

    private final ModSettingCategory modSettingCategory;
    private final List<HybridComponent> modSettingComponents = new ArrayList<>();
    private boolean extended;

    public ModCategoryComponent(ModSettingCategory modSettingCategory) {
        this.modSettingCategory = modSettingCategory;
        this.extended = false;

        for (ModSetting<?> setting : modSettingCategory.settings()) {
            if (setting instanceof BooleanSetting)
                modSettingComponents.add(new BooleanComponent((BooleanSetting) setting));
            if (setting instanceof NumberSetting)
                modSettingComponents.add(new NumberComponent((NumberSetting) setting));
        }
    }


    private int getNoneExtendedHeight() {
        return 34;
    }
    public int getTotalHeight() {


        if (!extended) {
            return getNoneExtendedHeight();
        }

        int spacing = 4;
        int extraBottomPadding = 15;

        int totalContentHeight = 0;
        for (HybridComponent component : modSettingComponents) {
            totalContentHeight += getDefaultHeight(component);
        }

        if (!modSettingComponents.isEmpty()) {
            totalContentHeight += spacing * (modSettingComponents.size() - 1);
        }

        return getNoneExtendedHeight()
                + spacing
                + totalContentHeight
                + extraBottomPadding;
    }

    private int computeExpandedHeight() {

        int spacing = 4;

        int totalContentHeight = 0;
        for (HybridComponent component : modSettingComponents) {
            totalContentHeight += getDefaultHeight(component);
        }

        if (!modSettingComponents.isEmpty()) {
            totalContentHeight += spacing * (modSettingComponents.size() - 1);
        }

        return getNoneExtendedHeight()
                + spacing
                + totalContentHeight;
    }

    @Override
    public void setupBounds() {

        componentBounds = outerBounds.copy();

        componentBounds.setHeight(
                extended
                        ? computeExpandedHeight()
                        : getNoneExtendedHeight()
        );

        super.setupBounds();
    }


    @Override
    public void render(HybridRenderer hybridRenderer) {

        if (extended) componentBounds.setHeight(componentBounds.getHeight() + 15);
        hybridRenderer.drawQuad(componentBounds, Theme.modBackgroundColor);

        HybridRenderText title = HybridTextRenderer.getTextRenderer(
                modSettingCategory.name(),
                FontStyle.BOLD,
                25,
                Color.WHITE,
                true
        );

        HybridRenderText toggleIcon = HybridTextRenderer.getIconRenderer(extended ? "up" : "down", Color.WHITE,componentBounds.getY());

        int headerCenterY = componentBounds.getY() + getNoneExtendedHeight() / 2;

        title.setPosition(
                componentBounds.getX() + Theme.xPadding,
                headerCenterY - title.getHeight() / 2
        );

        toggleIcon.setPosition(
                componentBounds.getX()
                        + componentBounds.getWidth()
                        - Theme.xPadding
                        - toggleIcon.getWidth(),
                headerCenterY - toggleIcon.getHeight() / 2
        );

        HybridTextRenderer.addText(title);
        HybridTextRenderer.addText(toggleIcon);

        if (!extended) return;

        int spacing = 4;
        int innerPadding = Theme.xPadding;

        int bgWidth = componentBounds.getWidth() - (Theme.xPadding * 2);
        int bgX = componentBounds.getX() + (componentBounds.getWidth() - bgWidth) / 2;

        int startY =
                componentBounds.getY()
                        + getNoneExtendedHeight()
                        + spacing;

        int totalContentHeight = computeExpandedHeight()
                - getNoneExtendedHeight()
                - spacing;

        ScreenBounds background = new ScreenBounds(
                bgX,
                startY,
                bgWidth,
                totalContentHeight
        );

        hybridRenderer.drawOutlineQuad(
                background,
                Theme.modsBackgroundColor,
                Theme.modButtonOutlineColor,
                10,
                1
        );

        int contentWidth = bgWidth - (innerPadding * 2);
        int contentX = bgX + (bgWidth - contentWidth) / 2;

        int currentY = background.getY();

        for (HybridComponent component : modSettingComponents) {

            int height = getDefaultHeight(component);

            component.outerBounds = new ScreenBounds(
                    contentX,
                    currentY,
                    contentWidth,
                    height
            );

            component.componentBounds = component.outerBounds.copy();

            component.renderPre(hybridRenderer);
            component.render(hybridRenderer);

            boolean isLast = component == modSettingComponents.getLast();

            if (!isLast) {
                ScreenBounds line = component.outerBounds.copy();
                line.setSize(
                        componentBounds.getWidth() - (Theme.xPadding * 2),
                        1
                );
                line.setPosition(
                        component.outerBounds.getX() - Theme.xPadding,
                        component.outerBounds.getY() + component.outerBounds.getHeight()
                );
                hybridRenderer.drawHorizontalLine(line, Theme.uiOutlineColor, 0.6f);
            }

            currentY += height;

            if (!isLast) {
                currentY += spacing;
            }
        }
    }

    @Override
    public void onMouseRelease(Click click) {

        ScreenBounds headerBounds = componentBounds.copy();
        headerBounds.setHeight(getNoneExtendedHeight());

        if (headerBounds.contains(click.x(), click.y())) {
            extended = !extended;
        }

        modSettingComponents.forEach(hybridComponent -> hybridComponent.onMouseRelease(click));

        super.onMouseRelease(click);
    }

    @Override
    public void onMouseClicked(Click click) {
        modSettingComponents.forEach(hybridComponent -> hybridComponent.onMouseClicked(click));

        super.onMouseClicked(click);
    }

    @Override
    public void onMouseDrag(Click click) {
        modSettingComponents.forEach(hybridComponent -> hybridComponent.onMouseDrag(click));

        super.onMouseDrag(click);
    }

    private int getDefaultHeight(HybridComponent component) {
        if (component instanceof BooleanComponent) return 30;
        if (component instanceof NumberComponent) return 30;
        return 24;
    }
}
