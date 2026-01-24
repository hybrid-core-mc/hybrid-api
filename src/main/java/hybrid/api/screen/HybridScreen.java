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

    public void addComponent(ComponentCategory component) {
        componentCategories.add(component);
    }

    public void registerComponents() {
    }



    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setCentered(context.getScaledWindowWidth(), context.getScaledWindowHeight());

        HybridRenderer.RENDERER_INSTANCE.fillQuad(bounds, Theme.backgroundColor);

        int globalY = bounds.getY() + 20;

        for (ComponentCategory componentCategory : componentCategories) {

            int categorySpacing = Theme.categorySpacing;

            int categoryHeight = 0;

            for (Component component : componentCategory.getComponents()) {
                categoryHeight += component.getBounds().getHeight();
                categoryHeight += categorySpacing;
            }

            int componentWidth = bounds.getWidth() - Theme.componentSpacing;

            int componentX = bounds.getX() + Theme.componentSpacing / 2;

            HybridRenderer.RENDERER_INSTANCE.fillQuad(new ScreenBounds(componentX, globalY, componentWidth, categoryHeight), Theme.componenetBackgroundColor);

            int compY = globalY;
            for (Component component : componentCategory.getComponents()) {
                component.getBounds().setY(compY);
                component.render(HybridRenderer.RENDERER_INSTANCE);

                compY += component.getBounds().getHeight() + categorySpacing;
            }

            globalY += categoryHeight + categorySpacing;
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }


}
