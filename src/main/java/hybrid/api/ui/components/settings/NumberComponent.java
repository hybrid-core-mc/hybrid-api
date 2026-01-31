package hybrid.api.ui.components.settings;

import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.ui.components.HybridComponent;

import java.awt.*;

public class NumberComponent extends HybridComponent {
    NumberSetting numberSetting;

    public NumberComponent(NumberSetting numberSetting) {
        this.numberSetting = numberSetting;
    }



    @Override
    public void render(HybridRenderer hybridRenderer) {
        hybridRenderer.drawQuad(componentBounds, Color.PINK, 0);

    }
}
