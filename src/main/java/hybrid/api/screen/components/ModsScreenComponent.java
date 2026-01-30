package hybrid.api.screen.components;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMods;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.Theme;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModsScreenComponent extends ScreenComponent {

    public static List<ModButton> buttons = new ArrayList<>();

    public ModsScreenComponent() {
        if (!buttons.isEmpty()) return;

        for (String mod : HybridMods.mods) {
            buttons.add(new ModButton(mod));
        }
    }



    @Override
    public void setupBounds() {
        int leftMenuWidth = (int) (outerBounds.getWidth() * 0.24);

        componentBounds = outerBounds.from(outerBounds);

        componentBounds.setWidth(leftMenuWidth);

        setComponentBounds(componentBounds);

    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        int offset = 0;

        int buttonWidth = (int) (componentBounds.getWidth() * 0.8);
        int buttonHeight = (int) (componentBounds.getHeight() * 0.085);
        int buttonSpacing = 5;

        int totalHeight = (buttonHeight * buttons.size()) + (buttonSpacing * (buttons.size() - 1));

        int centerY = componentBounds.getY() + (componentBounds.getHeight() - totalHeight) / 2;
        int centerX = componentBounds.getX() + (componentBounds.getWidth() - buttonWidth) / 2;


        for (ModButton button : buttons) {

            ScreenBounds bounds = new ScreenBounds(
                    centerX,
                    centerY + offset,
                    buttonWidth,
                    buttonHeight
            );

            button.render(hybridRenderer, bounds);

            offset += buttonHeight + buttonSpacing;
        }
        HybridRenderText renderText = HybridTextRenderer.getIconRenderer("expand", 5, 5, Color.RED);
        HybridTextRenderer.addText(renderText);
    }


    @Override
    public void renderPost(HybridRenderer hybridRenderer) {

        hybridRenderer.drawQuad(componentBounds, Theme.modsBackgroundColor);

        ScreenBounds leftSlice = componentBounds.from(componentBounds);
        leftSlice.setWidth(Theme.cornerRadius);
        leftSlice.setX(componentBounds.getX() + componentBounds.getWidth() - Theme.cornerRadius);

        hybridRenderer.drawQuad(leftSlice, Theme.modsBackgroundColor, 0);

        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                "Hybrid Core",
                FontStyle.EXTRABOLD,
                26,
                Color.WHITE
                , true
        );


        int offset = (int) (componentBounds.getHeight() * 0.18);

        int topLineY = componentBounds.getY() + offset;
        int bottomLineY = componentBounds.getY() + componentBounds.getHeight() - offset;

        float magic = 0.5f;
        hybridRenderer.drawHorizontalLine(
                new ScreenBounds(componentBounds.getX(), topLineY, componentBounds.getWidth(), 1),
                Theme.modButtonOutlineColor,magic
        );

        hybridRenderer.drawHorizontalLine(
                new ScreenBounds(componentBounds.getX(), bottomLineY, componentBounds.getWidth(), 1),
                Theme.modButtonOutlineColor,magic
        );

        int textX = componentBounds.getX()
                + (componentBounds.getWidth() - text.getWidth()) / 2;


        int bandTop = componentBounds.getY();
        int bandHeight = topLineY - bandTop;


        int textY = bandTop + (bandHeight - text.getHeight()) / 2;

        text.setPosition(textX, textY);
        HybridTextRenderer.addText(text);
        super.renderPost(hybridRenderer);
    }


    public static class ModButton { // todo: make thsi a real componenet XD
        String name;

        public ModButton(String name) {
            this.name = name;
        }

        public void render(HybridRenderer renderer, ScreenBounds bounds) {


            if(name == "Mono Bao"){
                renderer.drawOutlineQuad(bounds,Theme.modBackgroundColor,Theme.modButtonOutlineColor,10,1);
            } else {
                renderer.drawQuad(bounds, Theme.modBackgroundColor, 10);
            }
            int circleSize = 6;
            int padding = 8;

            int circleX = bounds.getX() + padding;
            int circleY = bounds.getY() + (bounds.getHeight() - circleSize) / 2;

            renderer.drawCircle(
                    new ScreenBounds(circleX, circleY, circleSize, circleSize),
                    Color.GREEN
            );

            HybridRenderText text = HybridTextRenderer.getTextRenderer(
                    name,
                    FontStyle.BOLD,
                    20,
                    Color.WHITE
            );

            int textX = circleX + circleSize + padding;
            int textY = bounds.getY() + (bounds.getHeight() - text.getHeight()) / 2;

            text.setPosition(textX, textY);
            HybridTextRenderer.addText(text);
        }

        public void onClick() {

        }
    }
}
