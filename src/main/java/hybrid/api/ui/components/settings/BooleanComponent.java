package hybrid.api.ui.components.settings;


import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.ui.components.HybridComponent;

import java.awt.*;

public class BooleanComponent extends HybridComponent {

    BooleanSetting booleanSetting;

    public BooleanComponent(BooleanSetting booleanSetting) {
        this.booleanSetting = booleanSetting;
    }


    @Override
    public void setupBounds() {

        super.setupBounds();
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {
        hybridRenderer.drawOutlineQuad(componentBounds,booleanSetting.get() ? Color.GREEN : Color.RED ,Color.WHITE,1,1);
    }
}
