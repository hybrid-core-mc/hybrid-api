package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.*;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridTheme;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
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
        this.extended = false;

        for (ModSetting<?> setting : modSettingCategory.settings()) {
            if (setting instanceof BooleanSetting)
                modSettingComponents.add(new BooleanComponent((BooleanSetting) setting));
            if (setting instanceof NumberSetting)
                modSettingComponents.add(new NumberComponent((NumberSetting) setting));
            if (setting instanceof ModeSetting<?>)
                modSettingComponents.add(new ModeComponent((ModeSetting<?>) setting));
            if (setting instanceof ColorSetting)
                modSettingComponents.add(new ColorComponent((ColorSetting) setting));
        }

        float collapsed = getCollapsedHeight();
        this.heightAnim = new PositionAnimation(collapsed, 0.2f);
        this.heightAnim.snap(collapsed);
    }


    private int getCollapsedHeight() {
        return 34;
    }

    private int getExpandedHeight() {
        int spacing = 4;
        int total = 0;

        for (HybridComponent c : modSettingComponents) {
            total += getDefaultHeight(c);
        }

        if (!modSettingComponents.isEmpty()) {
            total += spacing * (modSettingComponents.size() - 1);
        }

        return getCollapsedHeight() + spacing + total + 15;
    }

    public int getTotalHeight() {
        return (int) heightAnim.get();
    }


    @Override
    public void setupBounds() {
        componentBounds = outerBounds.copy();
        componentBounds.setHeight(getTotalHeight());
        super.setupBounds();
    }

    @Override
    public void render(HybridRenderer renderer) {

        heightAnim.update();
        componentBounds.setHeight((int) heightAnim.get());

        renderer.drawQuad(componentBounds, HybridThemeMap.get(ThemeColorKey.modBackgroundColor));

        HybridRenderText title = HybridTextRenderer.getTextRenderer(
                modSettingCategory.name(),
                FontStyle.BOLD,
                25,
                Color.WHITE,
                true
        );

        HybridRenderText toggleIcon = HybridTextRenderer.getIconRenderer(
                extended ? "up" : "down",
                Color.WHITE,
                componentBounds.getY()
        );

        int headerCenterY = componentBounds.getY() + getCollapsedHeight() / 2;

        title.setPosition(
                componentBounds.getX() + HybridTheme.xPadding,
                headerCenterY - title.getHeight() / 2
        );

        toggleIcon.setPosition(
                componentBounds.getX() + componentBounds.getWidth()
                        - HybridTheme.xPadding - toggleIcon.getWidth(),
                headerCenterY - toggleIcon.getHeight() / 2
        );

        HybridTextRenderer.addText(title);
        HybridTextRenderer.addText(toggleIcon);

        if (heightAnim.get() <= getCollapsedHeight() + 2)
            return;



        int spacing = 4;
        int innerPadding = HybridTheme.xPadding;

        int bgWidth = componentBounds.getWidth() - (HybridTheme.xPadding * 2);
        int bgX = componentBounds.getX() + HybridTheme.xPadding;

        int startY = componentBounds.getY() + getCollapsedHeight() + spacing;

        int maxContentHeight = getExpandedHeight() - getCollapsedHeight() - spacing - 15;
        int visibleContentHeight = Math.min(
                maxContentHeight,
                (int) heightAnim.get() - getCollapsedHeight() - spacing - 15
        );

        if (visibleContentHeight <= 0) return;

        ScreenBounds background = new ScreenBounds(
                bgX,
                startY,
                bgWidth,
                visibleContentHeight
        );

        renderer.drawOutlineQuad(
                background,
                HybridThemeMap.get(ThemeColorKey.modsBackgroundColor),
                HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                10,
                1
        );

        int contentWidth = bgWidth - (innerPadding * 2);
        int contentX = bgX + innerPadding;

        int currentY = background.getY();

        for (HybridComponent component : modSettingComponents) {

            int height = getDefaultHeight(component);

            if (currentY + height > background.getY() + background.getHeight())
                break;

            component.outerBounds = new ScreenBounds(contentX, currentY, contentWidth, height);
            component.componentBounds = component.outerBounds.copy();

            component.renderPre(renderer);
            component.render(renderer);

            boolean last = component == modSettingComponents.getLast();

            if (!last) {
                ScreenBounds line = component.outerBounds.copy();
                line.setSize(bgWidth, 1);
                line.setPosition(bgX, component.outerBounds.getY() + height);
                renderer.drawHorizontalLine(line, HybridThemeMap.get(ThemeColorKey.uiOutlineColor), 0.6f);
            }

            currentY += height + spacing;
        }
    }


    @Override
    public void onMouseRelease(Click click) {

        ScreenBounds header = componentBounds.copy();
        header.setHeight(getCollapsedHeight());

        if (header.contains(click.x(), click.y())) {
            extended = !extended;
            heightAnim.setTarget(
                    extended ? getExpandedHeight() : getCollapsedHeight()
            );
        }

        modSettingComponents.forEach(c -> c.onMouseRelease(click));
        super.onMouseRelease(click);
    }

    @Override
    public void onMouseClicked(Click click) {
        modSettingComponents.forEach(c -> c.onMouseClicked(click));
        super.onMouseClicked(click);
    }

    @Override
    public void onMouseDrag(Click click) {
        modSettingComponents.forEach(c -> c.onMouseDrag(click));
        super.onMouseDrag(click);
    }



    private int getDefaultHeight(HybridComponent component) {
        return component instanceof ColorComponent ? 100 : 30;
    }
}