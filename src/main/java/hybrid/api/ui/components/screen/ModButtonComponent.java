package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMod;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.animation.AlphaAnimation;

import java.awt.*;

public class ModButtonComponent {

    HybridMod mod;
    private boolean selected;
    private ScreenBounds bounds;
    AlphaAnimation animation;

    public ModButtonComponent(HybridMod name) {
        this.mod = name;
        animation = new AlphaAnimation(0f,0.2f);
    }

    public void render(HybridRenderer renderer, ScreenBounds bounds) {

        this.bounds = bounds;

        animation.update();

        renderer.drawQuad(bounds, HybridThemeMap.get(ThemeColorKey.modBackgroundColor),10);

        float alpha = animation.get();

        if(alpha > 0.001f){


            renderer.drawOutlineQuad(
                    bounds,
                    HybridThemeMap.get(ThemeColorKey.modBackgroundColor),
                    animation.withAlpha(HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor)),
                    6,
                    1
            );
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

        text.setPosition(textX,textY);
        HybridTextRenderer.addText(text);
    }

    public ScreenBounds getBounds() {
        return bounds;
    }

    public void setSelected(boolean b) {
        this.selected = b;
        animation.setTarget(b ? 1f : 0f);
    }

    public boolean isSelected(){
        return selected;
    }
}