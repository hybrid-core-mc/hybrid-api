package hybrid.api.screen.components;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMods;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.Theme;

import java.awt.*;

public class ModsScreenComponent extends ScreenComponent {

    @Override
    public void setupBounds() {
        int leftMenuWidth = (int) (outerBounds.getWidth() * 0.24);

        componentBounds = outerBounds.from(outerBounds);

        componentBounds.setWidth(leftMenuWidth);

        setComponentBounds(componentBounds);

    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        int modsBackgroundWidth = componentBounds.getWidth() - 8;
        int boxHeight = 22;
        int currentY = 5;

        for (String mod : HybridMods.mods) {

            int boxX = outerBounds.getX() + (componentBounds.getWidth() - modsBackgroundWidth) / 2;

            ScreenBounds backgroundBox = new ScreenBounds(boxX, outerBounds.getY() + currentY, modsBackgroundWidth - 15, boxHeight);

            hybridRenderer.drawQuad(backgroundBox, Theme.modBackgroundColor, 8);


            HybridRenderText text = HybridTextRenderer.getTextRenderer(mod, FontStyle.BOLD, 21, Color.WHITE);

            int textY = backgroundBox.getY() + (backgroundBox.getHeight() - text.getHeight()) / 2;

            text.setPosition(outerBounds.getX() + 7, textY);
            HybridTextRenderer.addText(text);


            int circleSize = 3;

            int circleX = (componentBounds.getX() + componentBounds.getWidth()) - (5 + 25);
            int circleY = backgroundBox.getY() + (backgroundBox.getHeight() - circleSize) / 2;

            hybridRenderer.drawCircle(new ScreenBounds(circleX, circleY, circleSize, circleSize), Color.GREEN);

            currentY += 29;
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
}
