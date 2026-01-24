package hybrid.api.componenets;

import hybrid.api.rendering.HybridRenderer2D;
import hybrid.api.rendering.ScreenBounds;

import java.awt.*;

public class ToggleButtonComponent extends Component {

    public ToggleButtonComponent() {
        super("toggle-button");
    }

    @Override
    public void render(HybridRenderer2D renderer) {
        getBounds().setPosition(getBounds().getX(),getBounds().getY());
        renderer.fillQuad(bounds, Color.GREEN);
    }
}
