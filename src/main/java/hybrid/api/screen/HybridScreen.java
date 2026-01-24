package hybrid.api.screen;

import hybrid.api.rendering.HybridRenderQueue;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;

public abstract class HybridScreen extends Screen {

    ScreenBounds bounds;
;
    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));
        bounds = new ScreenBounds(width, height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {


        HybridRenderQueue.add(renderer -> {

            renderer.fillQuad(new ScreenBounds(mouseX, mouseY, 5, 5), Color.RED);

        });

        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
