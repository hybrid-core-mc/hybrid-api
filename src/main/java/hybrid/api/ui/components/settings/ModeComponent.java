package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ModeComponent extends HybridComponent {
    ModeSetting<?> modeSetting;
    ScreenBounds modeBox;
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

        HybridRenderText modeText = HybridTextRenderer.getTextRenderer(
                modeSetting.get().name(),
                FontStyle.BOLD,
                18,
                Color.LIGHT_GRAY,
                new Color(140, 140, 140, 255),
                true
        );

        modeBox = getScreenBounds(modeText);

        hybridRenderer.drawOutlineQuad(modeBox,
                Theme.modBackgroundColor,
                Theme.modButtonOutlineColor,
                5,
                1
        );

        modeText.setPosition(modeBox.getX() + (modeBox.getWidth() - modeText.getWidth()) / 2, modeBox.getY() + (modeBox.getHeight() - modeText.getHeight()) / 2
        );

        HybridTextRenderer.addText(modeText);
    }

    private @NotNull ScreenBounds getScreenBounds(HybridRenderText modeText) {
        int paddingX = 10;
        int boxWidth = modeText.getWidth() + paddingX * 2;

        int boxHeight = (int) (componentBounds.getHeight() * 0.75);

        int boxX =
                componentBounds.getX()
                        + componentBounds.getWidth()
                        - boxWidth;

        int boxY =
                componentBounds.getY()
                        + (componentBounds.getHeight() - boxHeight) / 2;

        return new ScreenBounds(boxX, boxY-1, boxWidth, boxHeight);
    }

    @Override
    public void onMouseRelease(Click click) {
        super.onMouseRelease(click);

        if (modeBox == null) return;

        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        if (!modeBox.contains(mouseX, mouseY)) return;

        if(click.button() == 0){
            modeSetting.cycle();
        } else if(click.button() == 1) modeSetting.cycleBack();


    }
}
