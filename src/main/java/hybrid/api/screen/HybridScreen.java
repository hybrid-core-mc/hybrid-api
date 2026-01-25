package hybrid.api.screen;

import hybrid.api.componenets.Component;
import hybrid.api.componenets.ComponentCategory;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class HybridScreen extends Screen {

    ScreenBounds bounds;
    List<ComponentCategory> componentCategories = new ArrayList<>();

    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));

        bounds = new ScreenBounds(width, height);

        registerComponents();
    }

    public void registerComponent(ComponentCategory component) {
        componentCategories.add(component);
    }

    public void registerComponents() {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setCentered(context.getScaledWindowWidth(), context.getScaledWindowHeight());

        HybridRenderer.RENDERER_INSTANCE.fillQuad(bounds, Theme.backgroundColor);

        int globalY = bounds.getY() + Theme.compHeightSpacing;

        for (ComponentCategory componentCategory : componentCategories) {

            int categorySpacing = Theme.categoryHeightSpacing;
            int innerHeight = 0;

            for (Component component : componentCategory.getComponents()) {
                innerHeight += component.getBounds().getHeight();
                innerHeight += categorySpacing;
            }

            if (!componentCategory.getComponents().isEmpty()) {
                innerHeight -= categorySpacing;
            }

            int paddedCategoryHeight = innerHeight + (Theme.compHeightSpacing * 2);

            int componentBackgroundWidth = bounds.getWidth() - Theme.componentSpacing;
            int componentX = bounds.getX() + Theme.componentSpacing / 2;

            HybridRenderer.RENDERER_INSTANCE.fillQuad(
                    new ScreenBounds(componentX, globalY, componentBackgroundWidth, paddedCategoryHeight),
                    Theme.componenetBackgroundColor
            );

            int freeSpace = paddedCategoryHeight - innerHeight;
            int compY = globalY + (freeSpace / 2);


            for (Component component : componentCategory.getComponents()) {

                int componentWidth = componentBackgroundWidth - Theme.compWidthSpacing;

                component.getBounds().setWidth(componentWidth);

                int centeredCompX = componentX + (componentBackgroundWidth - componentWidth) / 2;

                component.getBounds().setPosition(centeredCompX, compY);

                component.render(HybridRenderer.RENDERER_INSTANCE);

                compY += component.getBounds().getHeight() + categorySpacing;
            }

            globalY += paddedCategoryHeight + categorySpacing;
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }


}
