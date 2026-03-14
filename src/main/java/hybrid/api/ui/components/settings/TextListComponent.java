package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.TextListSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.components.settings.global.TextBoxComponent;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

import java.awt.*;

public class TextListComponent extends SettingComponent {

    private final TextListSetting setting;
    TextBoxComponent textBoxComponent;

    public TextListComponent(TextListSetting setting) {
        this.setting = setting;
        textBoxComponent = new TextBoxComponent();
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

        int addWidth = 20;
        int spacing = 2;

        int textBoxX = componentBounds.getX() + componentBounds.getWidth() - boxWidth;

        int addX = textBoxX - addWidth - spacing;

        ScreenBounds addBounds = new ScreenBounds(
                addX,
                boxY,
                addWidth,
                boxHeight
        );

        textBoxComponent.componentBounds = new ScreenBounds(
                textBoxX,
                boxY,
                boxWidth,
                boxHeight
        );

        hybridRenderer.drawOutlineQuad(
                addBounds,
                HybridThemeMap.get(ThemeColorKey.modBackgroundColor),
                HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                4,
                1
        );

        textBoxComponent.render(hybridRenderer);

        int boxCenterY = boxY + boxHeight / 2;
        int labelY = boxCenterY - label.getHeight() / 2;

        label.setPosition(componentBounds.getX(), labelY);
        HybridTextRenderer.addText(label);

        setHeight(60);

        super.render(hybridRenderer);
    }
    @Override
    public void onMouseDrag(Click click) {
        textBoxComponent.onMouseDrag(click);
        super.onMouseDrag(click);
    }


    @Override
    public void onMouseRelease(Click click) {
        textBoxComponent.onMouseRelease(click);
        super.onMouseRelease(click);
    }

    @Override
    public void onMouseClicked(Click click) {
        textBoxComponent.onMouseClicked(click);
        super.onMouseClicked(click);
    }

    @Override
    public void onCharTyped(CharInput input) {
        textBoxComponent.onCharTyped(input);
        super.onCharTyped(input);
    }

    @Override
    public void keyPressed(KeyInput input) {
        textBoxComponent.keyPressed(input);
        super.keyPressed(input);
    }
}