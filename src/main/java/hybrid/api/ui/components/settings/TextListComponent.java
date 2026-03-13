package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.TextListSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class TextListComponent extends SettingComponent {

    private final TextListSetting setting;
    private String textBoxText = "";

    private ScreenBounds textBoxBounds;

    public TextListComponent(TextListSetting setting) {
        this.setting = setting;
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        HybridRenderText label = HybridTextRenderer.getTextRenderer(
                setting.getName(),
                FontStyle.BOLD,
                20,
                Color.WHITE,
                new Color(140, 140, 140, 255),
                true
        );

        int boxWidth = (int) (componentBounds.getWidth() * 0.3);
        int boxHeight = label.getHeight() + 12;

        int boxY = (int) (componentBounds.getY() + (componentBounds.getHeight() - boxHeight) * 0.25);
        int boxX = componentBounds.getX() + componentBounds.getWidth() - boxWidth;

        textBoxBounds = new ScreenBounds(
                boxX,
                boxY,
                boxWidth,
                boxHeight
        );

        hybridRenderer.drawOutlineQuad(
                textBoxBounds,
                HybridThemeMap.get(ThemeColorKey.modBackgroundColor),
                HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                4,
                1
        );

        int boxCenterY = boxY + boxHeight / 2;
        int labelY = boxCenterY - label.getHeight() / 2;

        label.setPosition(componentBounds.getX(), labelY);
        HybridTextRenderer.addText(label);

      
        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                textBoxText,
                FontStyle.BOLD,
                20,
                Color.WHITE,
                Color.GRAY,
                true
        );
        int textY = boxCenterY - text.getHeight() / 2;
        int textX = boxX + 6;


        text.setPosition(textX, textY);

        HybridRenderer.CONTEXT_LIST.add((ctx, renderer) -> {

            ctx.enableScissor(
                    textBoxBounds.getX(),
                    textBoxBounds.getY(),
                    textBoxBounds.getX() + textBoxBounds.getWidth() - 1,
                    textBoxBounds.getY() + textBoxBounds.getHeight()
            );

            text.draw(ctx);

            ctx.disableScissor();
        });

        setHeight(60);

        super.render(hybridRenderer);
    }


    @Override
    public void onCharTyped(CharInput input) {

        if (input.isValidChar()) {
            textBoxText += new String(Character.toChars(input.codepoint()));
        }

        super.onCharTyped(input);
    }

    @Override
    public void keyPressed(KeyInput input) {
        if (input.getKeycode() == GLFW.GLFW_KEY_BACKSPACE && !textBoxText.isEmpty()) {
            textBoxText = textBoxText.substring(0, textBoxText.length() - 1);
        }
        super.keyPressed(input);
    }
}