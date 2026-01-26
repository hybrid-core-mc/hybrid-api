package hybrid.api.screen.components;

import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;

public abstract class ScreenComponent {
    public ScreenBounds componentBounds, outerBounds;

    public void setupBounds() {
    }

    public void setComponentBounds(ScreenBounds componentBounds) {
        this.componentBounds = componentBounds;
    }

    public void setOuterBounds(ScreenBounds outerBounds) {
        this.outerBounds = outerBounds;
    }

    public abstract void render(HybridRenderer hybridRenderer);

    public void renderPost(HybridRenderer hybridRenderer) {

    }
}
