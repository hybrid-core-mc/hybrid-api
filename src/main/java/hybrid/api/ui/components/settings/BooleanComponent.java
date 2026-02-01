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
        int textY = (bounds.getY() + (bounds.getHeight() - text.getHeight()) / 2)-3;

        text.setPosition(textX, textY);

        ScreenBounds line = bounds.copy();

        line.setSize(componentBounds.getWidth() + Theme.xPadding * 2, 1);
        line.setPosition(bounds.getX() - Theme.xPadding, bounds.getY() + bounds.getHeight());
        hybridRenderer.drawHorizontalLine(line, Theme.uiOutlineColor, 0.6f);
        HybridTextRenderer.addText(text);

        // todo remove the magic constantsss XD

        int toggleButtonWidth = 38;
        ScreenBounds toggleBounds = componentBounds.copy();
        toggleBounds.setPosition((componentBounds.getX() + componentBounds.getWidth()) - toggleButtonWidth, componentBounds.getY() + 2);
        toggleBounds.setSize(toggleButtonWidth, (int) (componentBounds.getHeight() * 0.65));

        hybridRenderer.drawOutlineQuad(toggleBounds, Theme.modBackgroundColor, Theme.modButtonOutlineColor, 9, 1);
        toggleBounds.setX(toggleBounds.getX() + 5);
        toggleBounds.setWidth(10);
        hybridRenderer.drawCircle(toggleBounds, Color.LIGHT_GRAY);

    }

}
