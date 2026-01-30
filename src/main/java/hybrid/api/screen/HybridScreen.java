package hybrid.api.screen;

import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.screen.components.ModsScreenComponent;
import hybrid.api.ui.Theme;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HybridScreen extends Screen {

    private final ScreenBounds bounds;
    private ModsScreenComponent modsScreenComponent;
    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));
        this.bounds = new ScreenBounds(width, height);
        modsScreenComponent = new ModsScreenComponent();
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setCentered(context.getScaledWindowWidth(), context.getScaledWindowHeight());

        modsScreenComponent.setOuterBounds(bounds);
        modsScreenComponent.setupBounds();

        HybridRenderer renderer = HybridRenderer.RENDERER_INSTANCE;


        renderer.drawQuad(bounds, Theme.backgroundColor);

        modsScreenComponent.render(renderer);

        modsScreenComponent.renderPost(renderer);


        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return super.mouseClicked(click, doubled);
    }
}
