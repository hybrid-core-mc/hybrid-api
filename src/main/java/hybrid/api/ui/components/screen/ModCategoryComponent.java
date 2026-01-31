package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModCategoryComponent extends HybridComponent {

    private final ModSettingCategory modSettingCategory;
    private final List<HybridComponent> components = new ArrayList<>();

    public ModCategoryComponent(ModSettingCategory modSettingCategory) {
        this.modSettingCategory = modSettingCategory;

        for (ModSetting<?> setting : modSettingCategory.settings()) {
            if (setting instanceof BooleanSetting) components.add(new BooleanComponent((BooleanSetting) setting));
            if (setting instanceof NumberSetting) components.add(new NumberComponent((NumberSetting) setting));
        }
    }

    @Override
    public void setupBounds() {

        componentBounds = outerBounds.copy();

        int paddingX = 12;
        int paddingY = 10;
        int spacing = 6;

        int currentY = componentBounds.getY() + paddingY;
        int centerX = componentBounds.getX() + componentBounds.getWidth() / 2;

        for (HybridComponent component : components) {

            component.componentBounds = outerBounds.copy();

            int width = outerBounds.getWidth();
            int height = getDefaultHeight(component);

            int x = centerX - width / 2;

            component.outerBounds = new ScreenBounds(x, currentY, width, height);
            component.setupBounds();

            currentY += height + spacing;
        }

        componentBounds.setHeight(currentY - componentBounds.getY() + paddingY);

        super.setupBounds();
    }


    @Override
    public void render(HybridRenderer hybridRenderer) {

        hybridRenderer.drawQuad(componentBounds, Theme.modsBackgroundColor);

        HybridTextRenderer.addText(modSettingCategory.name(), FontStyle.REGULAR, 14, componentBounds.getX(), componentBounds.getY(), Color.RED);
        for (HybridComponent hybridComponent : components) {
            hybridComponent.renderPre(hybridRenderer);
            hybridComponent.render(hybridRenderer);
        }
    }


    private int getDefaultHeight(HybridComponent component) {
        if (component instanceof BooleanComponent) return 22;
        if (component instanceof NumberComponent) return 26;
        return 24;
    }
}
