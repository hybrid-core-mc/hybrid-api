package hybrid.api.screen;

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


        ScreenBounds leftSlice = bounds.from(bounds);

        leftSlice.setWidth(bounds.getWidth() / 4);

        RENDERER.fillQuad(bounds, Theme.backgroundColor);


        RENDERER.fillQuad(leftSlice, Theme.modsBackgroundColor);

        leftSlice.setX(leftSlice.getX() + leftSlice.getWidth() - Theme.cornerRadius);

        leftSlice.setWidth(Theme.cornerRadius);

        RENDERER.fillQuad(leftSlice, Theme.modsBackgroundColor, 0);

        int modsBackgroundWidth = (bounds.getWidth() / 4) - Theme.modsSpacing;
        int boxHeight = 24;
        int currentY = 5;

        for (String mods : HybridMods.mods) {
            RENDERER.fillQuad(new ScreenBounds(bounds.getX(), bounds.getY() + currentY, modsBackgroundWidth, boxHeight), Color.WHITE,5);
            HybridTextRenderer.addText(mods,15, bounds.getX(), bounds.getY()+currentY,Color.BLUE);
            currentY += 29;
        }


        super.render(context, mouseX, mouseY, deltaTicks);
    }


}
