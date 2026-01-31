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
    private final List<HybridComponent> components = new ArrayList<>();
     boolean extended;

    public ModCategoryComponent(ModSettingCategory modSettingCategory) {
        this.modSettingCategory = modSettingCategory;
        this.extended = false;

        for (ModSetting<?> setting : modSettingCategory.settings()) {
            if (setting instanceof BooleanSetting) components.add(new BooleanComponent((BooleanSetting) setting));
            if (setting instanceof NumberSetting) components.add(new NumberComponent((NumberSetting) setting));
        }

        System.out.println("init the category settings comp");

    }

    @Override
    public void setupBounds() {

        componentBounds = outerBounds.copy();

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


        componentBounds.setHeight(extended ? 80 : 34);

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

        int textY = componentBounds.getY() + getNoneExtendedHeight() / 2 - text.getHeight() / 2;

        int iconY = componentBounds.getY() + getNoneExtendedHeight() / 2 - toggleIndicator.getHeight() / 2;

        text.setPosition(componentBounds.getX() + Theme.xPadding, textY);

        toggleIndicator.setPosition(componentBounds.getX() + componentBounds.getWidth() - (Theme.xPadding + 2) - toggleIndicator.getWidth(), iconY);

        HybridTextRenderer.addText(text);
        HybridTextRenderer.addText(toggleIndicator);

        if (extended) {

//            components.forEach(hybridComponent -> {
//                hybridComponent.renderPre(hybridRenderer);
//                hybridComponent.render(hybridRenderer);
//            });

        }
    }


    private int getDefaultHeight(HybridComponent component) {
        if (component instanceof BooleanComponent) return 22;
        if (component instanceof NumberComponent) return 26;
        return 24;
    }

    @Override
    public void onMouseRelease(Click click) {

        if (componentBounds.contains(click.x(), click.y())) {

            extended = !extended;

        }

        super.onMouseRelease(click);
    }

}
