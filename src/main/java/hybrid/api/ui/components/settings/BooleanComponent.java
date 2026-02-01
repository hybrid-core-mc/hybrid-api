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

        // ---------- Shared vertical center ----------
        int centerY = bounds.getY() + bounds.getHeight() / 2;

        // ---------- Text ----------
        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                booleanSetting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );

        int textX = bounds.getX() + 4;
        int textY = centerY - text.getHeight() / 2;

        text.setPosition(textX, textY+1);
        HybridTextRenderer.addText(text);

        int toggleButtonWidth = 38;
        int toggleButtonHeight = (int) (bounds.getHeight() * 0.65);

        int toggleX = bounds.getX()
                + bounds.getWidth()
                - toggleButtonWidth;

        int toggleY = centerY - toggleButtonHeight / 2;

        ScreenBounds toggleBounds = new ScreenBounds(
                toggleX,
                toggleY,
                toggleButtonWidth,
                toggleButtonHeight
        );

        hybridRenderer.drawOutlineQuad(
                toggleBounds,
                Theme.modBackgroundColor,
                Theme.modButtonOutlineColor,
                9,
                1
        );

        int knobSize = 10;

        ScreenBounds knob = new ScreenBounds(
                toggleBounds.getX() + 5,
                centerY - knobSize / 2,
                knobSize,
                knobSize
        );

        hybridRenderer.drawCircle(knob, Color.LIGHT_GRAY);
    }


}
