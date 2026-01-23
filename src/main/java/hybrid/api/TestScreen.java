package hybrid.api;

import hybrid.api.rendering.HybridRenderQueue;
import hybrid.api.rendering.HybridRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;

public class TestScreen extends Screen {
    public TestScreen() {
        super(Text.of("hi"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        HybridRenderQueue.add(renderer -> {
            renderer.fillQuad(50, 50, 500, 500, Color.BLUE);
        });
        HybridRenderer.render();

        super.render(context, mouseX, mouseY, deltaTicks);
    }
}
