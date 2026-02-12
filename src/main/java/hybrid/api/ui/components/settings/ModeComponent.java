package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridTheme;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.animation.PositionAnimation;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ModeComponent extends HybridComponent {

    private final ModeSetting<?> modeSetting;
    private ScreenBounds modeBox;

    private  PositionAnimation boxAnimation = new PositionAnimation(1.0f, 0.8f);

    private boolean pressed = false;

    private int cachedBoxWidth = -1;

    public ModeComponent(ModeSetting<?> modeSetting) {
        this.modeSetting = modeSetting;
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        HybridRenderText label = HybridTextRenderer.getTextRenderer(
                modeSetting.getName(),
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

        int textSize = pressed ? 16 : 18;

        HybridRenderText modeText = HybridTextRenderer.getTextRenderer(
                modeSetting.get().name(),
                FontStyle.BOLD,
                textSize,
                Color.LIGHT_GRAY,
                new Color(140, 140, 140, 255),
                true
        );

        modeBox = getScreenBounds();

        boxAnimation.update();

        float scale = boxAnimation.get();

        float cx = modeBox.getX() + modeBox.getWidth() / 2f;
        float cy = modeBox.getY() + modeBox.getHeight() / 2f;

        float scaledW = modeBox.getWidth() * scale;
        float scaledH = modeBox.getHeight() * scale;

        ScreenBounds scaledBox = new ScreenBounds(
                (int) (cx - scaledW / 2f),
                (int) (cy - scaledH / 2f),
                (int) scaledW,
                (int) scaledH
        );

        hybridRenderer.drawOutlineQuad(
                scaledBox,
                HybridThemeMap.get(ThemeColorKey.modBackgroundColor),
                HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                5,
                1
        );

        modeText.setPosition(
                scaledBox.getX() + (scaledBox.getWidth() - modeText.getWidth()) / 2,
                scaledBox.getY() + (scaledBox.getHeight() - modeText.getHeight()) / 2
        );

        HybridTextRenderer.addText(modeText);
    }



    private @NotNull ScreenBounds getScreenBounds() {

        int paddingX = 10;
        int boxHeight = (int) (componentBounds.getHeight() * 0.75);

        if (cachedBoxWidth == -1) {
            String longest = modeSetting.getLongestValue();

            HybridRenderText measure = HybridTextRenderer.getTextRenderer(
                    longest,
                    FontStyle.BOLD,
                    18,
                    Color.LIGHT_GRAY,
                    new Color(140, 140, 140, 255),
                    true
            );

            cachedBoxWidth = measure.getWidth() + paddingX * 2;
        }

        int boxX =
                componentBounds.getX()
                        + componentBounds.getWidth()
                        - cachedBoxWidth;

        int boxY =
                componentBounds.getY()
                        + (componentBounds.getHeight() - boxHeight) / 2;

        return new ScreenBounds(boxX, boxY , cachedBoxWidth, boxHeight);
    }


    @Override
    public void onMouseClicked(Click click) {
        if (modeBox != null && modeBox.contains((int) click.x(), (int) click.y())) {
            pressed = true;
            boxAnimation.setTarget(0.92f);
        }
        super.onMouseClicked(click);
    }

    @Override
    public void onMouseRelease(Click click) {
        super.onMouseRelease(click);

        pressed = false;
        boxAnimation.setTarget(1.0f);

        if (modeBox == null) return;
        if (!modeBox.contains((int) click.x(), (int) click.y())) return;

        if (click.button() == 0) {
            modeSetting.cycle();
        } else if (click.button() == 1) {
            modeSetting.cycleBack();
        }
    }
}