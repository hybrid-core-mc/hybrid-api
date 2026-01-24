package hybrid.api.screen;

import hybrid.api.componenets.Component;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.HybridRenderer2D;
import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class HybridScreen extends Screen {

    ScreenBounds bounds;
    List<Component> components = new ArrayList<>();

    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));
        bounds = new ScreenBounds(width, height);

        components.add(new Component("background") {
            @Override
            public void render(HybridRenderer2D renderer) {
                renderer.fillQuad(bounds, Color.WHITE);
            }
        });

        registerComponents();
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void registerComponents() {

    }

    public void updateComponentPositions() {
        int currentY = 3;

        for (Component component : components) {
            component.getBounds().setPosition(bounds.getX(), bounds.getY() + currentY);
            component.getBounds().setWidth(bounds.getWidth());
            currentY += 3;
        }

    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setPosition((context.getScaledWindowWidth() - bounds.getWidth()) / 2, (context.getScaledWindowHeight() - bounds.getHeight()) / 2);
        updateComponentPositions();


        for (Component component : components) {
            component.render(HybridRenderer.RENDERER_INSTANCE);
        }


        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
