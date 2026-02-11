package hybrid.api.ui.components;

import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gui.Click;

public abstract class HybridComponent {
    public ScreenBounds componentBounds;
    public ScreenBounds outerBounds;

    public void setupBounds() {
    }

    public abstract void render(HybridRenderer hybridRenderer);

    public void onMouseRelease(Click click) {

    }

    public void onMouseClicked(Click click) {

    }
    public void onMouseDrag(Click click) {

    }
    public void onMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount){

    }

    public void renderPre(HybridRenderer hybridRenderer) {

    }


}
