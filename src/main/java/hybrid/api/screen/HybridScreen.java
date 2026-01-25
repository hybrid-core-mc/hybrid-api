package hybrid.api.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMods;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.Theme;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;

public class HybridScreen extends Screen {

    private ScreenBounds bounds;
    private ScreenCategoryBuilder built;

    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));
        this.bounds = new ScreenBounds(width, height);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        bounds.setCentered(context.getScaledWindowWidth(), context.getScaledWindowHeight());

        HybridRenderer renderer = HybridRenderer.RENDERER_INSTANCE;

        int leftMenuWidth = (int) (bounds.getWidth() * 0.24);
        ScreenBounds leftSlice = bounds.from(bounds);
        leftSlice.setWidth(leftMenuWidth);

        renderer.drawQuad(bounds, Theme.backgroundColor);
        renderer.drawQuad(leftSlice, Theme.modsBackgroundColor);

        leftSlice.setX(leftSlice.getX() + leftSlice.getWidth() - Theme.cornerRadius);
        leftSlice.setWidth(Theme.cornerRadius);
        renderer.drawQuad(leftSlice, Theme.modsBackgroundColor, 0);

        int modsBackgroundWidth = leftMenuWidth - 4;
        int boxHeight = 22;
        int currentY = 5;

        for (String mods : HybridMods.mods) {

            int boxX = bounds.getX() + (leftMenuWidth - modsBackgroundWidth) / 2;
            ScreenBounds backgroundBox = new ScreenBounds(boxX, bounds.getY() + currentY, modsBackgroundWidth, boxHeight);

            HybridRenderText text = HybridTextRenderer.getTextRenderer(mods, FontStyle.BOLD, 21, Color.BLUE);

            renderer.drawQuad(backgroundBox, Color.ORANGE);

            int textY = bounds.getY() + currentY + (boxHeight - text.getHeight()) / 2;
            text.setPosition(bounds.getX() + 5, textY);

            renderer.drawCircle(new ScreenBounds(leftSlice.getX() - 5, (boxX + (boxHeight - 3) / 2) / 2, 3, 3), Color.GREEN);

            HybridTextRenderer.addText(text);
            currentY += 29;
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return super.mouseClicked(click, doubled);
    }
}
