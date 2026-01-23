package hybrid.api.componenets;

import hybrid.api.rendering.HybridRenderQueue;

import java.awt.*;

public class BooleanComponent extends Component {

    public BooleanComponent() {
        super("boolean.toggle");
    }

    public void render() {
        HybridRenderQueue.add(renderer ->
                renderer.fillQuad(5, 5, 100, 100, Color.GREEN)
        );
    }
}
