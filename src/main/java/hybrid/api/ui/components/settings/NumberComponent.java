package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;

import java.awt.*;

public class NumberComponent extends HybridComponent {
    NumberSetting numberSetting;

    public NumberComponent(NumberSetting numberSetting) {
        this.numberSetting = numberSetting;
    }



    @Override
    public void render(HybridRenderer hybridRenderer) {

        ScreenBounds bounds = componentBounds;

        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                numberSetting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );

        int textX = bounds.getX();
        int textY = bounds.getY()
                + (bounds.getHeight() - text.getHeight()) / 2;

        text.setPosition(textX, textY);

        ScreenBounds line = bounds.copy();
        line.setHeight(1);
        line.setY(bounds.getY() + bounds.getHeight());
        HybridTextRenderer.addText(text);


        int valueX = ((textX + text.getWidth() + 5));
        HybridRenderText valueText = HybridTextRenderer.getTextRenderer(String.valueOf(numberSetting.get()), FontStyle.REGULAR, 20, Color.WHITE, new Color(140, 140, 140, 255), false);
        valueText.setPosition(valueX, textY);
        HybridTextRenderer.addText(valueText);



        ScreenBounds sliderLine = componentBounds.copy();
        sliderLine.setSize(100, 10);
        sliderLine.setPosition((int) (((bounds.getX() + bounds.getWidth())) - (componentBounds.getWidth() * 0.39)), textY);

        hybridRenderer.drawOutlineQuad(sliderLine, Theme.modBackgroundColor, Theme.modButtonOutlineColor, 4, 1);
        int fillWidth = 50;
        sliderLine.setWidth(fillWidth);
        hybridRenderer.drawOutlineQuad(sliderLine, Theme.modButtonOutlineColor.darker(), Theme.modButtonOutlineColor, 4, 1);

        ScreenBounds sliderCircle = componentBounds.copy();
        int circleScale = 10;
        sliderCircle.setWidth(circleScale);
        sliderCircle.setX(sliderLine.getX() + fillWidth - circleScale);
        hybridRenderer.drawCircle(sliderCircle, Color.LIGHT_GRAY);
    }

    @Override
    public void onMouseRelease(Click click) {
        super.onMouseRelease(click);
    }

    @Override
    public void onMouseClicked(Click click) {
        System.out.println("we clicked");
        super.onMouseClicked(click);
    }
}
