package hybrid.api.ui.components.settings;


import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.components.HybridComponent;

import java.awt.*;

public class BooleanComponent extends HybridComponent {

    BooleanSetting booleanSetting;

    public BooleanComponent(BooleanSetting booleanSetting) {
        this.booleanSetting = booleanSetting;
    }


    @Override
    public void render(HybridRenderer hybridRenderer) {

        ScreenBounds bounds = componentBounds;

        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                booleanSetting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );

        int textX = bounds.getX() + 4;
        int textY = bounds.getY()
                + (bounds.getHeight() - text.getHeight()) / 2;

        text.setPosition(textX, textY);

        ScreenBounds line = bounds.copy();
        line.setHeight(1);
        line.setY(bounds.getY() + bounds.getHeight());

        hybridRenderer.drawHorizontalLine(line, Theme.uiOutlineColor, 0.5f);

        HybridTextRenderer.addText(text);
    }

}
