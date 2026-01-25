package hybrid.api.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMods;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;

public class HybridScreen extends Screen {

    ScreenBounds bounds;
    ScreenCategoryBuilder built;


    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));

        bounds = new ScreenBounds(width, height);

    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setCentered(context.getScaledWindowWidth(), context.getScaledWindowHeight());

        HybridRenderer RENDERER = HybridRenderer.RENDERER_INSTANCE;


        int leftMenuWidth = (int) (bounds.getWidth() * 0.24);

        ScreenBounds leftSlice = bounds.from(bounds);

        leftSlice.setWidth(leftMenuWidth);

        RENDERER.fillQuad(bounds, Theme.backgroundColor);


        RENDERER.fillQuad(leftSlice, Theme.modsBackgroundColor);

        leftSlice.setX(leftSlice.getX() + leftSlice.getWidth() - Theme.cornerRadius);

        leftSlice.setWidth(Theme.cornerRadius);

        RENDERER.fillQuad(leftSlice, Theme.modsBackgroundColor, 0);

        int modsBackgroundWidth = leftMenuWidth - 4;
        int boxHeight = 22;
        int currentY = 5;

        for (String mods : HybridMods.mods) {

            int boxX = bounds.getX() + (leftMenuWidth - modsBackgroundWidth) / 2;


            HybridRenderText text = HybridTextRenderer.getTextRenderer(mods, FontStyle.BOLD, 21, boxX, bounds.getY() + currentY, Color.BLUE);

            RENDERER.fillQuad(new ScreenBounds(boxX, bounds.getY() + currentY, modsBackgroundWidth, boxHeight), Color.WHITE, 5);
            int textY = bounds.getY() + currentY + (boxHeight - text.getHeight()) / 2;
            text.setPosition(bounds.getX() + 5,textY);
            HybridTextRenderer.addText(text);

            currentY += 29;
        }


        super.render(context, mouseX, mouseY, deltaTicks);
    }


}
