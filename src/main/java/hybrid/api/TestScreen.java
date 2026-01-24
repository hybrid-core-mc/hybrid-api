package hybrid.api;

import hybrid.api.rendering.HybridRenderQueue;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.screen.HybridScreen;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;

public class TestScreen extends HybridScreen {
    public TestScreen() {
        super("test",500,300);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
//        context.fill(mouseX,mouseY,mouseX+5,mouseY+5,-1);

        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
