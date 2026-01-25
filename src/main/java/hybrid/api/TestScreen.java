package hybrid.api;

import hybrid.api.componenets.ClickButtonComponent;
import hybrid.api.componenets.ComponentCategory;
import hybrid.api.componenets.ToggleButtonComponent;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.screen.HybridScreen;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class TestScreen extends HybridScreen {
    public TestScreen() {
        super("test",300,180);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        HybridRenderText heading = HybridTextRenderer.addText("hii world", 15, mouseX, mouseY, Color.GREEN);
        heading.draw(context);
        HybridRenderText xd = HybridTextRenderer.addText("moaning bear", 50, mouseX, mouseY + 15, Color.BLACK);
        xd.draw(context);
        context.fill(mouseX, mouseY, mouseX + heading.getWidth(), mouseY + (heading.getHeight()), new Color(255, 1, 1, 100).getRGB());
        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public void registerComponents() {

        System.out.println("pre register");
        ComponentCategory test = new ComponentCategory("Hey");

        test.addComponent(new ToggleButtonComponent());
        test.addComponent(new ClickButtonComponent());

        registerComponent(test);

        ComponentCategory testing = new ComponentCategory("test");
        testing.addComponent(new ToggleButtonComponent());
        test.addComponent(new ClickButtonComponent());

        registerComponent(test);

        super.registerComponents();
    }


}
