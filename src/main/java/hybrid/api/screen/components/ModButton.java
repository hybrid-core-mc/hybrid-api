package hybrid.api.screen.components;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.Theme;

import java.awt.*;
import java.util.Objects;

public class ModButton { // todo: make thsi a real componenet XD
    String name;

    public ModButton(String name) {
        this.name = name;
    }

    public void render(HybridRenderer renderer, ScreenBounds bounds) {


        if (Objects.equals(name, "Mono Bao")) {
            renderer.drawOutlineQuad(bounds, Theme.modBackgroundColor, Theme.modButtonOutlineColor, 10, 1);
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