package hybrid.api.screen.components;

import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gui.Click;

public abstract class ScreenComponent {
    public ScreenBounds componentBounds;
    public static ScreenBounds outerBounds;

    public void setupBounds() {
    }

    public void setComponentBounds(ScreenBounds componentBounds) {
        this.componentBounds = componentBounds;
    }

    public void setOuterBounds(ScreenBounds outerBounds) {
        ScreenComponent.outerBounds = outerBounds;
    }

    public abstract void renderPost(HybridRenderer hybridRenderer);

    public void onMouseRelease(Click click) {

    }
    public void render(HybridRenderer hybridRenderer) {

    }
}
