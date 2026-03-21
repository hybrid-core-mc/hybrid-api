package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mod.settings.ButtonSetting;
import hybrid.api.rendering.HybridRenderer;

import java.awt.*;

public class ButtonComponent extends SettingComponent {
    ButtonSetting buttonSetting;

    public ButtonComponent(ButtonSetting buttonSetting) {
        this.buttonSetting = buttonSetting;
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {
        HybridRenderText label = HybridTextRenderer.getTextRenderer(
                buttonSetting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );

        int centerY = componentBounds.getY() + componentBounds.getHeight() / 2;

        label.setPosition(
                componentBounds.getX(),
                centerY - label.getHeight() / 2
        );



        HybridTextRenderer.addText(label);

        super.render(hybridRenderer);
    }
}
