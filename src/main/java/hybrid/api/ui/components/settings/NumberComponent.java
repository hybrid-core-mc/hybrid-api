package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.animation.PositionAnimation;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.gui.Click;

import java.awt.*;

public class NumberComponent extends HybridComponent {

    private final PositionAnimation fillAnimation = new PositionAnimation(0f, 0.7f);
    private final NumberSetting numberSetting;
    private boolean dragging = false;
    private ScreenBounds sliderBounds;

    public NumberComponent(NumberSetting numberSetting) {
        this.numberSetting = numberSetting;
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        ScreenBounds bounds = componentBounds;
        int centerY = bounds.getY() + bounds.getHeight() / 2;

        HybridRenderText label = HybridTextRenderer.getTextRenderer(
                numberSetting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );

        label.setPosition(bounds.getX(), centerY - label.getHeight() / 2);
        HybridTextRenderer.addText(label);


         int sliderWidth = 100;
         int sliderHeight = 10;
         int sliderRadius = 4;
         int strokeWidth = 1;
         int sliderCircleSize = 10;
        
        int sliderX = bounds.getX() + bounds.getWidth() - sliderWidth;
        int sliderY = centerY - sliderHeight / 2;

        sliderBounds = new ScreenBounds(sliderX, sliderY, sliderWidth, sliderHeight);

        hybridRenderer.drawOutlineQuad(sliderBounds, HybridThemeMap.get(ThemeColorKey.modBackgroundColor), HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor), sliderRadius, strokeWidth);

        double min = numberSetting.getMin();
        double max = numberSetting.getMax();
        double value = numberSetting.get();

        double percent = (value - min) / (max - min);
        percent = Math.max(0.0, Math.min(1.0, percent));

        float targetWidth = (float) (sliderWidth * percent);

        if (dragging) {
            fillAnimation.setTarget(targetWidth);
            fillAnimation.update();
        } else {
            fillAnimation.setTarget(targetWidth);
            fillAnimation.update();
        }

        int fillWidth = (int) fillAnimation.get();

        ScreenBounds fill = sliderBounds.copy();
        fill.setWidth(fillWidth);

        hybridRenderer.drawOutlineQuad(fill, HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor).darker(), HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor), sliderRadius - 1,
                strokeWidth);


        int knobX = sliderX + fillWidth - sliderCircleSize / 2;
        int knobY = centerY - sliderCircleSize / 2;

        ScreenBounds knobBounds = new ScreenBounds(knobX, knobY, sliderCircleSize, sliderCircleSize);

        hybridRenderer.drawCircle(knobBounds, Color.LIGHT_GRAY);



        if (dragging) {

            HybridRenderText valueText = HybridTextRenderer.getTextRenderer(String.valueOf(numberSetting.get()), FontStyle.REGULAR, 14, Color.WHITE, true);

            valueText.setPosition(knobBounds.getX() + sliderCircleSize / 2 - valueText.getWidth() / 2, knobBounds.getY() - valueText.getHeight() - 4);

            HybridTextRenderer.addText(valueText);
        }

    }


    @Override
    public void onMouseClicked(Click click) {

        if (sliderBounds != null && sliderBounds.contains(click.x(), click.y())) {
            dragging = true;
            updateValueFromMouse(click.x());
        }

        super.onMouseClicked(click);
    }

    @Override
    public void onMouseRelease(Click click) {
        dragging = false;
        super.onMouseRelease(click);
    }

    @Override
    public void onMouseDrag(Click click) {

        if (!dragging || sliderBounds == null) return;

        updateValueFromMouse(click.x());
    }


    private void updateValueFromMouse(double mouseX) {

        double minX = sliderBounds.getX();
        double maxX = sliderBounds.getX() + sliderBounds.getWidth();

        mouseX = Math.max(minX, Math.min(maxX, mouseX));

        double percent = (mouseX - minX) / (maxX - minX);

        double newValue = numberSetting.getMin() + percent * (numberSetting.getMax() - numberSetting.getMin());

        numberSetting.set((float) newValue);
    }
}
