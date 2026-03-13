package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMod;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;

import java.awt.*;

public class ModButtonComponent {

     HybridMod mod;
    boolean selected;

    public ModButtonComponent(HybridMod name) {
        this.mod = name;
    }
    private ScreenBounds bounds;



    public void render(HybridRenderer renderer, ScreenBounds bounds) {


        this.bounds = bounds;


        if (selected) {
            renderer.drawOutlineQuad(
                    bounds,
                    HybridThemeMap.get(ThemeColorKey.modBackgroundColor),
                    HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                    10,
                    1
            );
        } else {
            renderer.drawQuad(bounds, HybridThemeMap.get(ThemeColorKey.modBackgroundColor), 10);
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
                mod.getFormattedName(),
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

    public void setSelected(boolean b) {
        this.selected = b;
    }
}
