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

    private final ScreenBounds bounds;
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

        for (String mod : HybridMods.mods) {

            int boxX = bounds.getX() + (leftMenuWidth - modsBackgroundWidth) / 2;

            ScreenBounds backgroundBox = new ScreenBounds(boxX, bounds.getY() + currentY, modsBackgroundWidth, boxHeight);

            renderer.drawQuad(backgroundBox, Color.ORANGE, 8);



            HybridRenderText text = HybridTextRenderer.getTextRenderer(mod, FontStyle.BOLD, 21, Color.BLUE);

            int textY = backgroundBox.getY() + (backgroundBox.getHeight() - text.getHeight()) / 2;

            text.setPosition(bounds.getX() + 7, textY);
            HybridTextRenderer.addText(text);


            int circleSize = 3;

            int circleX = leftSlice.getX() - 5;
            int circleY = backgroundBox.getY() + (backgroundBox.getHeight() - circleSize) / 2;

            renderer.drawCircle(new ScreenBounds(circleX, circleY, circleSize, circleSize), Color.GREEN);

            currentY += 29;
        }

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return super.mouseClicked(click, doubled);
    }
}
