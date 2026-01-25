package hybrid.api.componenet.componenets;

import hybrid.api.componenet.Component;
import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.rendering.HybridRenderer2D;
import hybrid.api.settings.Setting;
import hybrid.api.settings.ToggleButtonSetting;

import java.awt.*;

public class ToggleButtonComponent extends Component {
    ToggleButtonSetting setting;

    public ToggleButtonComponent(Setting setting) {
        super(setting);
        this.setting = (ToggleButtonSetting) setting;
    }

    @Override
    public void render(HybridRenderer2D renderer) {

      //  HybridRenderText heading = HybridTextRenderer.addText(setting.getName(), FontStyle.BOLD, getBounds().getX(), getBounds().getY(),1,1);
    }
}
