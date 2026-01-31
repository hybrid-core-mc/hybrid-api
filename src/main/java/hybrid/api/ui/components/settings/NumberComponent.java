package hybrid.api.ui.components.settings;

import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.ui.components.HybridComponent;

public class NumberComponent extends HybridComponent {
    NumberSetting numberSetting;

    public NumberComponent(NumberSetting numberSetting) {
        this.numberSetting = numberSetting;
    }

    @Override
    public void setupBounds() {
        super.setupBounds();
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

    }
}
