package hybrid.api.screen;

import hybrid.api.componenets.Component;
import hybrid.api.componenets.ComponentCategory;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
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

        HybridRenderer.RENDERER_INSTANCE.fillQuad(bounds, new Color(240, 239, 245));

        int globalY = bounds.getY() + 5;

        for (ComponentCategory componentCategory : componentCategories) {

            int categorySpacing = 4;

            int categoryHeight = 0;
            for (Component component : componentCategory.getComponents()) {
                categoryHeight += component.getBounds().getHeight();
                categoryHeight += categorySpacing;
            }

            HybridRenderer.RENDERER_INSTANCE.fillQuad(new ScreenBounds(bounds.getX(), globalY, bounds.getWidth() - 5, categoryHeight), new Color(100, 149, 237));

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
