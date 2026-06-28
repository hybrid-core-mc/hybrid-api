package hybrid.api.mod.chat.parts;

import hybrid.api.Main;
import hybrid.api.mod.chat.parts.commands.CommandTreeHelper;
import hybrid.api.theme.ThemeManager;
import hybrid.api.theme.ThemeTarget;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;

import javax.swing.text.TabExpander;
import java.awt.*;

public class ChatBoxComponent {

    CommandTreeHelper helper;
    public ChatBoxComponent() {
        helper = new CommandTreeHelper("");
    }

    public void render(Quad quad, String commands, Quad typingBounds, int alpha) {


        int headingSpacing = 25;
        HybridRenderer2D.drawRoundRect(quad.copy().subtractY(headingSpacing).addHeight(headingSpacing), new Color(12, 15, 23, alpha), Color.GRAY, 8, 0.5f);
        Color base = new Color(24, 26, 36, alpha);

        float factor = 0.55f;

        int r = base.getRed() + (int) ((255 - base.getRed()) * factor);
        int g = base.getGreen() + (int) ((255 - base.getGreen()) * factor);
        int b = base.getBlue() + (int) ((255 - base.getBlue()) * factor);

        Color lighter = new Color(r, g, b, base.getAlpha());


        Quad line = quad.copy()
                        .addHeight(headingSpacing)
                        .setHeight(1)
                        .addX(5)
                        .subtractWidth(10)
                ;

        RenderContext.get().enableScissor(line.x, line.y, line.width + line.x, line.y + 1);
        HybridRenderer2D.drawRoundRect(line
                , new Color(0, 0, 0, 0), lighter
                , 0, 0.8f, 0, 0, 0, 0
        );
        RenderContext.get().disableScissor();

        Color base1 = new Color(ThemeManager.get(ThemeTarget.ACCENT).getRGB(), true);

        Color withAlpha = new Color(
                base1.getRed(),
                base1.getGreen(),
                base1.getBlue(),
                alpha
        );

        HybridRenderer2D.drawCircle(line.copy().subtractY(15).addX(5), 3f, withAlpha, alpha > 50);
        HybridRenderText text = HybridTextRenderer.getTextRenderer("HYBRID CHAT", FontStyle.EXTRABOLD,16,new Color(255, 255, 255,alpha),false);

        text.setPosition(line.x + 18, line.y - text.getHeight() - 8);
        HybridTextRenderer.addText(text);

        if (commands.startsWith("/")) {
            helper.setInput(commands.replaceFirst("/", ""));
            helper.update().thenAccept(suggestions -> {

                if (suggestions == null || suggestions.isEmpty()) return;

                float x = quad.x + 2;
                float y = quad.y + 2;

                Main.RENDERER.drawText(
                        Main.getStyle(),
                        commands,
                        x,
                        y,
                        12,
                        0,
                        0,
                        typingBounds
                );

                float offset = Main.getStyle().getWidth(commands, 12);

                float lineHeight = 12 + 2;

                for (int i = 0; i < suggestions.size(); i++) {

                    String suggestion = suggestions.get(i).getText();

                    Main.RENDERER.drawText(
                            Main.getStyle(),
                            suggestion,
                            x + offset,
                            y + (i * lineHeight),
                            12,
                            -1,
                            1,
                            null
                    );
                }
            });
        }

    }

}
