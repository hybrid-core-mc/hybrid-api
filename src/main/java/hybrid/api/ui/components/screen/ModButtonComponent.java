package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.Theme;

import java.awt.*;

public class ModButtonComponent { // todo: make this a real component XD

    private final String name;
    boolean selected;

    public ModButtonComponent(String name) {
        this.name = name;
    }
    private ScreenBounds bounds;

    public String getName() {
        return name;
    }

    public void render(HybridRenderer renderer, ScreenBounds bounds) {
        this.bounds = bounds;


        if (selected) {
            renderer.drawOutlineQuad(
                    bounds,
                    Theme.modBackgroundColor,
                    Theme.modButtonOutlineColor,
                    10,
                    1
            );
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


    public ScreenBounds getBounds() {
        return bounds;
    }

    public void onClick() {
        System.out.println("Clicked mod: " + name);
    }

    public void setSelected(boolean b) {
        this.selected = b;
    }
}
