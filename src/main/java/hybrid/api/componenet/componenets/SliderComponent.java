package hybrid.api.componenet.componenets;

import hybrid.api.componenet.Component;
import hybrid.api.rendering.HybridRenderer2D;
import hybrid.api.settings.Setting;

import java.awt.*;

public class SliderComponent extends Component {
    public SliderComponent(Setting setting) {
        super(setting);
    }

    @Override
    public void render(HybridRenderer2D renderer) {
        renderer.drawQuad(getBounds(), Color.BLUE);
    }
}
