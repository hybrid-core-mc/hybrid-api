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

    List<ModButton> buttons = new ArrayList<>();

    public ModsScreenComponent() {
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
        int buttonHeight = (int) (componentBounds.getHeight() * 0.095);
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
    }


    @Override
    public void renderPost(HybridRenderer hybridRenderer) {


        hybridRenderer.drawQuad(componentBounds, Theme.modsBackgroundColor);


        ScreenBounds leftSlice = componentBounds.from(componentBounds);

        leftSlice.setWidth(Theme.cornerRadius);

        leftSlice.setX(componentBounds.getX() + componentBounds.getWidth() - Theme.cornerRadius);

        hybridRenderer.drawQuad(leftSlice, Theme.modsBackgroundColor, 0);


        super.renderPost(hybridRenderer);
    }

    public static class ModButton { // todo: make thsi a real componenet XD
        String name;

        public ModButton(String name) {
            this.name = name;
        }

        public void render(HybridRenderer renderer, ScreenBounds bounds) {
            renderer.drawQuad(bounds, Theme.modBackgroundColor, 10);

            HybridRenderText text = HybridTextRenderer.getTextRenderer(name, FontStyle.BOLD, 21, Color.WHITE);

            int textY = bounds.getY() + (bounds.getHeight() - text.getHeight()) / 2;

            text.setPosition(bounds.getX() + 7, textY);

            HybridTextRenderer.addText(text);

            int circleSize = 3;

            int circleX = (int) ((bounds.getX() + bounds.getWidth()) - (bounds.getWidth()*0.1));
            int circleY = bounds.getY() + (bounds.getHeight() - circleSize) / 2;

            renderer.drawCircle(new ScreenBounds(circleX, circleY, circleSize, circleSize), Color.GREEN);

        }

        public void onClick() {

        }
    }
}
