package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.*;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.animation.PositionAnimation;
import hybrid.api.ui.components.HybridComponent;
import hybrid.api.ui.components.settings.*;
import net.minecraft.client.gui.Click;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModCategoryComponent extends HybridComponent {

    private final ModSettingCategory modSettingCategory;
    private final List<HybridComponent> modSettingComponents = new ArrayList<>();

    private boolean extended;

    private final PositionAnimation heightAnim;

    public ModCategoryComponent(ModSettingCategory modSettingCategory) {
        this.modSettingCategory = modSettingCategory;

        for (ModSetting<?> setting : modSettingCategory.settings()) {
            if (setting instanceof BooleanSetting)
                modSettingComponents.add(new BooleanComponent((BooleanSetting) setting));
            if (setting instanceof NumberSetting)
                modSettingComponents.add(new NumberComponent((NumberSetting) setting));
            if(setting instanceof ModeSetting<?>){
                modSettingComponents.add(new ModeComponent((ModeSetting<?>) setting));
            }
            if (setting instanceof ColorSetting) {
                modSettingComponents.add(new ColorComponent((ColorSetting) setting));
            }
        }
        this.heightAnim = new PositionAnimation(getCollapsedHeight(), 0.09f);
    }


    private int getCollapsedHeight() {
        return 34;
    }

    public int getTotalHeight() {
        if (!extended) return getCollapsedHeight();

        int spacing = 4;
        int extraBottomPadding = 15;

        int content = 0;
        for (HybridComponent c : modSettingComponents) {
            content += getDefaultHeight(c);
        }

        if (!modSettingComponents.isEmpty()) {
            content += spacing * (modSettingComponents.size() - 1);
        }

        return getCollapsedHeight() + spacing + content + extraBottomPadding;
    }

    public int getAnimatedHeight() {
        return (int) heightAnim.get();
    }


    @Override
    public void setupBounds() {
        componentBounds = outerBounds.copy();
        componentBounds.setHeight(getAnimatedHeight());
        super.setupBounds();
    }


    @Override
    public void render(HybridRenderer renderer) {

        heightAnim.setTarget(extended ? getTotalHeight() : getCollapsedHeight());
        heightAnim.update();

        componentBounds.setHeight(getAnimatedHeight());

        renderer.drawQuad(componentBounds, Theme.modBackgroundColor);

        HybridRenderText title = HybridTextRenderer.getTextRenderer(
                modSettingCategory.name(),
                FontStyle.BOLD,
                25,
                Color.WHITE,
                true
        );

        HybridRenderText toggleIcon = HybridTextRenderer.getIconRenderer(
                extended ? "up" : "down",
                Color.WHITE
        );

        int headerCenterY = componentBounds.getY() + getCollapsedHeight() / 2;

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

        if (getAnimatedHeight() <= getCollapsedHeight() + 1) return;

        int spacing = 4;
        int innerPadding = Theme.xPadding;

        int bgWidth = componentBounds.getWidth() - Theme.xPadding * 2;
        int bgX = componentBounds.getX() + (componentBounds.getWidth() - bgWidth) / 2;

        int startY = componentBounds.getY() + getCollapsedHeight() + spacing;
        int availableHeight = getAnimatedHeight() - getCollapsedHeight() - spacing;

        if (availableHeight <= 0) return;

        ScreenBounds background = new ScreenBounds(
                bgX,
                startY,
                bgWidth,
                availableHeight
        );

        renderer.drawOutlineQuad(
                background,
                Theme.modsBackgroundColor,
                Theme.modButtonOutlineColor,
                10,
                1
        );

        int contentWidth = bgWidth - innerPadding * 2;
        int contentX = bgX + (bgWidth - contentWidth) / 2;

        int currentY = background.getY();

        for (HybridComponent component : modSettingComponents) {

            int h = getDefaultHeight(component);
            if (currentY + h > background.getY() + background.getHeight()) break;

            component.outerBounds = new ScreenBounds(contentX, currentY, contentWidth, h);
            component.componentBounds = component.outerBounds.copy();

            component.renderPre(renderer);
            component.render(renderer);

            currentY += h + spacing;
        }
    }


    @Override
    public void onMouseRelease(Click click) {

        ScreenBounds headerBounds = componentBounds.copy();
        headerBounds.setHeight(getCollapsedHeight());

        if (headerBounds.contains(click.x(), click.y())) {
            extended = !extended;
        }

        modSettingComponents.forEach(c -> c.onMouseRelease(click));
        super.onMouseRelease(click);
    }

    private int getDefaultHeight(HybridComponent component) {
        return component instanceof ColorComponent ? 100 : 30;
    }
}