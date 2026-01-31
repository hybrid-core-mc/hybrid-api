package hybrid.api.ui;

import hybrid.api.mods.HybridMods;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.components.HybridComponent;
import hybrid.api.ui.components.screen.ModHybridComponent;
import hybrid.api.ui.components.screen.ModsHybridComponent;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HybridScreen extends Screen {

    private final ScreenBounds bounds;
    private final List<HybridComponent> components = new ArrayList<>();

    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));

        this.bounds = new ScreenBounds(0, 0, width, height);

        components.add(new ModsHybridComponent());
        components.add(new ModHybridComponent(HybridMods.mods.getFirst()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setCentered(
                context.getScaledWindowWidth(),
                context.getScaledWindowHeight()
        );

        HybridRenderer renderer = HybridRenderer.RENDERER_INSTANCE;


        for (HybridComponent component : components) {
            component.outerBounds = bounds;
            component.setupBounds();
            component.renderPre(renderer);
            component.render(renderer);
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseReleased(Click click) {

        for (HybridComponent component : components) {
            component.onMouseRelease(click);
        }

        return super.mouseReleased(click);
    }
}
