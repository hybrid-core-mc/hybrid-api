package hybrid.api.font;

import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class HybridTextRenderer {

    // otpimized to the t

    private static final Map<String, Font> fontCache = new HashMap<>();

    private static final Map<String, HybridRenderText> textCache = new HashMap<>();

    private static final String DEFAULT_FONT_NAME = "Arial";
    private static final int DEFAULT_FONT_STYLE = 1;


    public static HybridRenderText addText(String text, int size, int x, int y, Color color) {

        String fontKey = DEFAULT_FONT_NAME + "|" + DEFAULT_FONT_STYLE + "|" + size;

        Font font = fontCache.computeIfAbsent(fontKey, k -> new Font(DEFAULT_FONT_NAME, Font.BOLD, size));

        String textKey = text + "|" + font + "|" + color.getRGB();

        HybridRenderText renderText = textCache.get(textKey);

        if (renderText == null) {
            renderText = new HybridRenderText(text, x, y, font, color);
            textCache.put(textKey, renderText);
        } else {
            renderText.setPosition(x, y);
        }

        return renderText;
    }


    public void renderAll(DrawContext context) {
        textCache.values().forEach(hybridRenderText -> {
            hybridRenderText.draw(context);
        });
    }
}
