package hybrid.api.screen;

import hybrid.api.mods.HybridMods;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.screen.components.ModScreenComponent;
import hybrid.api.screen.components.ModsScreenComponent;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HybridScreen extends Screen {

    private final ScreenBounds bounds;
    private final List<ScreenComponent> components = new ArrayList<>();

    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));

        this.bounds = new ScreenBounds(0, 0, width, height);

        components.add(new ModsScreenComponent());
        components.add(new ModScreenComponent(HybridMods.mods.getFirst()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setCentered(
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight()
        );

        HybridRenderer renderer = HybridRenderer.RENDERER_INSTANCE;


        for (ScreenComponent component : components) {
            component.setOuterBounds(bounds);
            component.setupBounds();
            component.renderPre(renderer);
            component.render(renderer);
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseReleased(Click click) {

        for (ScreenComponent component : components) {
            component.onMouseRelease(click);
        }

        return super.mouseReleased(click);
    }
}
