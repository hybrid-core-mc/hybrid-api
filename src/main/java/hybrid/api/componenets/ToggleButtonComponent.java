package hybrid.api.componenets;

import hybrid.api.rendering.HybridRenderer2D;

import java.awt.*;

public class ToggleButtonComponent extends Component {

    public ToggleButtonComponent() {
        super("toggle-button");
    }

    @Override
    public void render(HybridRenderer2D renderer) {


//        bounds.setWidth(outerBounds.getWidth() - 6);
//
//
//        bounds.setX(bounds.getCenterX(outerBounds));
//
        renderer.fillQuad(bounds,Color.RED);

    }
}
