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
     boolean extended;

    public ModCategoryComponent(ModSettingCategory modSettingCategory) {
        this.modSettingCategory = modSettingCategory;
        this.extended = false;

        for (ModSetting<?> setting : modSettingCategory.settings()) {
            if (setting instanceof BooleanSetting)
                modSettingComponents.add(new BooleanComponent((BooleanSetting) setting));
            if (setting instanceof NumberSetting)
                modSettingComponents.add(new NumberComponent((NumberSetting) setting));
        }

        System.out.println("init the category settings comp");

    }

    @Override
    public void setupBounds() {

        componentBounds = outerBounds.copy();

        componentBounds.setHeight(extended ? 170 : 34);

        super.setupBounds();
    }

    public int getNoneExtendedHeight(){
        return 34;
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        hybridRenderer.drawQuad(componentBounds, Theme.modBackgroundColor);

        HybridRenderText text = HybridTextRenderer.getTextRenderer(modSettingCategory.name(), FontStyle.EXTRABOLD, 25, Color.WHITE, true);

        HybridRenderText toggleIndicator = HybridTextRenderer.getIconRenderer(extended ? "up" : "down", Color.WHITE);

        int headerCenterY = componentBounds.getY() + getNoneExtendedHeight() / 2;

        text.setPosition(componentBounds.getX() + Theme.xPadding, headerCenterY - text.getHeight() / 2);

        toggleIndicator.setPosition(componentBounds.getX() + componentBounds.getWidth() - (Theme.xPadding + 2) - toggleIndicator.getWidth(), headerCenterY - toggleIndicator.getHeight() / 2);

        HybridTextRenderer.addText(text);
        HybridTextRenderer.addText(toggleIndicator);

        if (!extended) return;

        int spacing = 4;
        int innerPadding = Theme.xPadding;

        int totalContentHeight = 0;
        for (HybridComponent component : modSettingComponents) {
            totalContentHeight += getDefaultHeight(component);
        }
        totalContentHeight += spacing * (modSettingComponents.size() - 1);

        int bgWidth = componentBounds.getWidth() - (Theme.xPadding * 2);

        int bgHeight = totalContentHeight;

        int bgX = componentBounds.getX() + (componentBounds.getWidth() - bgWidth) / 2;

        int availableHeight = componentBounds.getHeight() - getNoneExtendedHeight() - spacing;

        int startY = componentBounds.getY() + getNoneExtendedHeight() + spacing + (availableHeight - bgHeight) / 2;

        ScreenBounds background = new ScreenBounds(bgX, startY, bgWidth, bgHeight);

        hybridRenderer.drawOutlineQuad(background, Theme.modsBackgroundColor, Theme.modButtonOutlineColor, 10, 1);

        int contentWidth = bgWidth - (innerPadding * 2);
        int contentX = bgX + (bgWidth - contentWidth) / 2;

        int currentY = background.getY() + (background.getHeight() - totalContentHeight) / 2;

        for (HybridComponent component : modSettingComponents) {

            int height = getDefaultHeight(component);

            component.outerBounds = new ScreenBounds(contentX, currentY, contentWidth, height);

            component.componentBounds = component.outerBounds.copy();

            component.renderPre(hybridRenderer);
            component.render(hybridRenderer);

            boolean isLast = modSettingComponents.getLast() == component;

            if (!isLast) {
                ScreenBounds line = component.outerBounds.copy();
                line.setSize(componentBounds.getWidth() - (Theme.xPadding*2), 1);
                line.setPosition(component.outerBounds.getX() - Theme.xPadding, component.outerBounds.getY() + component.outerBounds.getHeight());
                hybridRenderer.drawHorizontalLine(line, Theme.uiOutlineColor, 0.6f);
            }
//            hybridRenderer.drawQuad(component.componentBounds,new Color(255,0,0,100),0);

            currentY += height;

            if (component != modSettingComponents.getLast()) {
                currentY += spacing;
            }
        }
    }


    private int getDefaultHeight(HybridComponent component) {
        if (component instanceof BooleanComponent) return 30;
        if (component instanceof NumberComponent) return 30;
        return 24;
    }

    @Override
    public void onMouseRelease(Click click) {

        ScreenBounds toggleBounds = componentBounds.copy();
        toggleBounds.setHeight(getNoneExtendedHeight());

        if (toggleBounds.contains(click.x(), click.y())) {

            extended = !extended;

        }

        super.onMouseRelease(click);
    }

}
