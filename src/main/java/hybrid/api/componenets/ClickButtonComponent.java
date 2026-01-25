package hybrid.api.componenets;

import hybrid.api.rendering.HybridRenderer2D;

import java.awt.*;

public class ClickButtonComponent extends Component{
    public ClickButtonComponent() {
        super("click-button");
    }

    @Override
    public void render(HybridRenderer2D renderer) {
        renderer.fillQuad(bounds, Color.BLUE);
    }
}
