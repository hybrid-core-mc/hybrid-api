package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.animation.PositionAnimation;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;

import java.awt.*;

public class BooleanComponent extends HybridComponent {

    private final BooleanSetting booleanSetting;
    PositionAnimation knobAnimation;
    private ScreenBounds toggleBounds;

    public BooleanComponent(BooleanSetting booleanSetting) {
        this.booleanSetting = booleanSetting;
        knobAnimation = new PositionAnimation(5f, 0.4f);
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        ScreenBounds bounds = componentBounds;
        int centerY = bounds.getY() + bounds.getHeight() / 2;

        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                booleanSetting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );

        text.setPosition(bounds.getX(), centerY - text.getHeight() / 2 + 1);
        HybridTextRenderer.addText(text);

        int toggleButtonWidth = 36;
        int toggleButtonHeight = (int) (bounds.getHeight() * 0.63);

        int toggleX = bounds.getX() + bounds.getWidth() - toggleButtonWidth;
        int toggleY = centerY - toggleButtonHeight / 2;

        toggleBounds = new ScreenBounds(toggleX, toggleY, toggleButtonWidth, toggleButtonHeight);

        hybridRenderer.drawOutlineQuad(
                toggleBounds,
                Theme.modBackgroundColor,
                Theme.modButtonOutlineColor,
                9,
                1
        );

        knobAnimation.setTarget(booleanSetting.get() ? 22f : 5f);
        knobAnimation.update();

        int knobSize = 10;
        float animatedOffset = knobAnimation.get();

        ScreenBounds knob = new ScreenBounds(
                toggleBounds.getX() + (int) animatedOffset,
                centerY - knobSize / 2,
                knobSize,
                knobSize
        );

        hybridRenderer.drawCircle(knob, Color.LIGHT_GRAY);
    }

    @Override
    public void onMouseRelease(Click click) {

        if (toggleBounds != null && toggleBounds.contains(click.x(), click.y())) {
            booleanSetting.toggle();
        }

        super.onMouseRelease(click);
    }
}