package hybrid.api.testmods.chatplus;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mod.category.ModCategory;
import hybrid.api.mod.category.ModCategorySettingBuilder;
import hybrid.api.mod.settings.BooleanSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.animation.AlphaAnimation;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static hybrid.api.HybridApi.mc;

public class InputCategory extends ModCategory {

    private final String[][] keys = {
            {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
            {"A", "S", "D", "F", "G", "H", "J", "K", "L"},
            {"Z", "X", "C", "V", "B", "N", "M"},
            {"SPACE"}
    };

    private final int[] rowOffsets = {0, 12, 24, 0};
    private final int spacing = 4;
    private final int spaceRowOffsetY = 15;

    private final Map<String, AlphaAnimation> keyAnimations = new HashMap<>();

    BooleanSetting keyboard = new BooleanSetting("Screen Keyboard", true);

    public InputCategory() {
        super("Input");
    }

    @Override
    public void build(ModCategorySettingBuilder builder) {
        builder.add(keyboard);
    }

    @Override
    public void onInitialize() {
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {

            if (!(screen instanceof ChatScreen)) return;

            ScreenEvents.afterRender(screen).register(this::afterRender);
            ScreenMouseEvents.afterMouseRelease(screen).register(this::mouseRelease);
        });

        super.onInitialize();
    }

    private AlphaAnimation getAnim(String key) {
        return keyAnimations.computeIfAbsent(key, k -> new AlphaAnimation(1f, 0.85f));
    }

    private boolean mouseRelease(Screen screen, Click click, boolean consumed) {
        if (!keyboard.get()) return consumed;
        if (click.button() != 0) return consumed;

        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        int startX = 20;
        int startY = 200;

        boolean clickedKey = false;

        int maxWidth = getRowWidth(keys[0]);

        for (int row = 0; row < keys.length; row++) {
            int xOffset = 0;

            for (int col = 0; col < keys[row].length; col++) {

                String key = keys[row][col];
                String display = key.equals("SPACE") ? " " : key;

                HybridRenderText text = HybridTextRenderer.getTextRenderer(
                        display, FontStyle.BOLD, 12, Color.WHITE
                );

                int width = getKeyWidth(key, text);
                int height = getKeyHeight(text);

                int x = (row == keys.length - 1)
                        ? startX + (maxWidth - width) / 2
                        : startX + rowOffsets[row] + xOffset;

                int y = startY + row * (height + spacing);
                if (row == keys.length - 1) y += spaceRowOffsetY;

                if (mouseX >= x && mouseX <= x + width &&
                        mouseY >= y && mouseY <= y + height) {

                    onKeyPress(key);

                    AlphaAnimation anim = getAnim(key);
                    anim.snap(0.2f);
                    anim.setTarget(1f);

                    clickedKey = true;
                }

                xOffset += width + spacing;
            }
        }

        return clickedKey || consumed;
    }

    private void afterRender(Screen screen, DrawContext context, int mouseX, int mouseY, float delta) {
        if (!keyboard.get()) return;

        HybridRenderer renderer = HybridRenderer.RENDERER_INSTANCE;

        int startX = 20;
        int startY = 200;

        int maxWidth = getRowWidth(keys[0]);
        int padding = 5;

        int totalHeight = keys.length * (getKeyHeight(
                HybridTextRenderer.getTextRenderer("A", FontStyle.BOLD, 12, Color.WHITE)
        ) + spacing);

        renderer.drawQuad(
                new ScreenBounds(startX - padding, startY - padding, maxWidth + padding * 2, totalHeight + padding * 2 - 7),
                new Color(20, 20, 20, 180),
                8
        );

        for (int row = 0; row < keys.length; row++) {
            int xOffset = 0;

            for (int col = 0; col < keys[row].length; col++) {

                String key = keys[row][col];
                String display = key.equals("SPACE") ? " " : key;

                AlphaAnimation anim = getAnim(key);
                anim.update();

                HybridRenderText text = HybridTextRenderer.getTextRenderer(
                        display, FontStyle.BOLD, 12, Color.WHITE
                );

                int textWidth = text.getWidth();
                int textHeight = text.getHeight();

                int width = getKeyWidth(key, text);
                int height = getKeyHeight(text);

                int x = (row == keys.length - 1)
                        ? startX + (maxWidth - width) / 2
                        : startX + rowOffsets[row] + xOffset;

                int y = startY + row * (height + spacing);
                if (row == keys.length - 1) y += spaceRowOffsetY;

                boolean hovered = mouseX >= x && mouseX <= x + width &&
                        mouseY >= y && mouseY <= y + height;

                Color base = hovered
                        ? new Color(255, 0, 0)
                        : new Color(35, 35, 35);

                Color bg = anim.withAlpha(base);

                renderer.drawQuad(new ScreenBounds(x, y, width, height), bg, 5);

                int textX = x + (width - textWidth) / 2;
                int textY = y + (height - textHeight) / 2;

                text.setPosition(textX, textY);
                text.draw(context);

                xOffset += width + spacing;
            }
        }
    }

    private void onKeyPress(String key) {
        if (!(mc.currentScreen instanceof ChatScreen chatScreen)) return;

        if (key.equals("SPACE")) {
            chatScreen.insertText(" ", false);
        } else {
            chatScreen.insertText(key, false);
        }
    }

    private int getKeyWidth(String key, HybridRenderText text) {
        int paddingX = 6;
        return key.equals("SPACE")
                ? (text.getWidth() + paddingX * 2) * 6
                : text.getWidth() + paddingX * 2;
    }

    private int getKeyHeight(HybridRenderText text) {
        int paddingY = 4;
        return text.getHeight() + paddingY * 2;
    }

    private int getRowWidth(String[] row) {
        int width = 0;

        for (int i = 0; i < row.length; i++) {
            String key = row[i];

            HybridRenderText text = HybridTextRenderer.getTextRenderer(
                    key.equals("SPACE") ? " " : key,
                    FontStyle.BOLD, 12, Color.WHITE
            );

            width += getKeyWidth(key, text);

            if (i < row.length - 1) width += spacing;
        }

        return width;
    }
}
