package hybrid.api.screen.components;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.HybridMod;
import hybrid.api.mods.HybridMods;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.Theme;
import net.minecraft.client.gui.Click;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModsScreenComponent extends ScreenComponent {

    public static List<ModButton> buttons = new ArrayList<>();
    private int topLineY;
    private int bottomLineY;
    private HybridRenderText titleText;
    private ScreenBounds titleBackground;

    public ModsScreenComponent() {
        if (!buttons.isEmpty()) return;

        for (HybridMod mod : HybridMods.mods) {
            buttons.add(new ModButton(mod.getName()));
        }
    }



    @Override
    public void setupBounds() {
        int leftMenuWidth = (int) (outerBounds.getWidth() * 0.24);

        componentBounds = outerBounds.from(outerBounds);

        componentBounds.setWidth(leftMenuWidth);

        setComponentBounds(componentBounds);

    }


    @Override
    public void renderPost(HybridRenderer hybridRenderer) {

        int offset = 0;

        int buttonWidth = (int) (componentBounds.getWidth() * 0.8);
        int buttonHeight = (int) (componentBounds.getHeight() * 0.085);
        int buttonSpacing = 5;

        int totalHeight = (buttonHeight * buttons.size()) + (buttonSpacing * (buttons.size() - 1));

        int centerY = componentBounds.getY() + (componentBounds.getHeight() - totalHeight) / 2;
        int centerX = componentBounds.getX() + (componentBounds.getWidth() - buttonWidth) / 2;


        for (ModButton button : buttons) {

            ScreenBounds bounds = new ScreenBounds(
                    centerX,
                    centerY + offset,
                    buttonWidth,
                    buttonHeight
            );

            button.render(hybridRenderer, bounds);

            offset += buttonHeight + buttonSpacing;
        }


    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        drawBackground(hybridRenderer);
        drawDividers(hybridRenderer);
        drawTitle(hybridRenderer);
        drawBottomIcons(hybridRenderer);

        super.render(hybridRenderer);
    }

    private void drawDividers(HybridRenderer r) {
        int dividerOffset = (int) (componentBounds.getHeight() * 0.23f);

        topLineY = componentBounds.getY() + dividerOffset;
        bottomLineY = componentBounds.getY() + componentBounds.getHeight() - dividerOffset;

        float fade = 0.5f;

        r.drawHorizontalLine(new ScreenBounds(componentBounds.getX(), topLineY, componentBounds.getWidth(), 1), Theme.modButtonOutlineColor, fade
        );

        r.drawHorizontalLine(new ScreenBounds(componentBounds.getX(), bottomLineY, componentBounds.getWidth(), 1), Theme.modButtonOutlineColor, fade);
    }

    private void drawTitle(HybridRenderer r) {
        titleText = HybridTextRenderer.getTextRenderer("Hybrid Core", FontStyle.EXTRABOLD, 25, Color.WHITE, true
        );

        int textX = componentBounds.getX() + (componentBounds.getWidth() - titleText.getWidth()) / 2;

        int bandHeight = topLineY - componentBounds.getY();
        int textY = componentBounds.getY() + (bandHeight - titleText.getHeight()) / 2;

        titleText.setPosition(textX, textY);
        HybridTextRenderer.addText(titleText);

        int padX = 8;
        int padY = 6;

        titleBackground = new ScreenBounds(textX - padX, textY - padY, titleText.getWidth() + padX * 2, titleText.getHeight() + padY * 2
        );

        r.drawOutlineQuad(titleBackground, Theme.modBackgroundColor, Theme.uiOutlineColor, 6, 1);
    }

    private void drawBottomIcons(HybridRenderer r) {
        HybridRenderText[] icons = {HybridTextRenderer.getIconRenderer("collapse", 0, 0, Color.WHITE), HybridTextRenderer.getIconRenderer("theme", 0, 0, Color.WHITE), HybridTextRenderer.getIconRenderer("settings", 0, 0, Color.WHITE)};

        int iconBox = 18;

        int bottomBandHeight = componentBounds.getY() + componentBounds.getHeight() - bottomLineY;

        int iconsY = bottomLineY + (bottomBandHeight - iconBox) / 2;

        ScreenBounds iconsBackground = new ScreenBounds(componentBounds.getX() + (componentBounds.getWidth() - titleBackground.getWidth()) / 2, iconsY - 4, titleBackground.getWidth(), iconBox + 8);

        r.drawOutlineQuad(iconsBackground, Theme.modBackgroundColor, Theme.uiOutlineColor, 6, 1);

        float step = titleText.getWidth() / (float) icons.length;
        float startCenterX = titleText.getX() + step / 2f;

        for (int i = 0; i < icons.length; i++) {
            HybridRenderText icon = icons[i];

            float centerX = startCenterX + i * step;

            icon.setPosition((int) (centerX - icon.getWidth() / 2f), iconsY + (iconBox - icon.getHeight()) / 2
            );

            HybridTextRenderer.addText(icon);
        }
    }

    private void drawBackground(HybridRenderer r) {
        r.drawQuad(componentBounds, Theme.modsBackgroundColor);

        ScreenBounds rightSlice = componentBounds.from(componentBounds);
        rightSlice.setWidth(Theme.cornerRadius);
        rightSlice.setX(componentBounds.getX() + componentBounds.getWidth() - Theme.cornerRadius);

        r.drawQuad(rightSlice, Theme.modsBackgroundColor, 0);
    }

    @Override
    public void onMouseRelease(Click click) {

        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        for (ModButton button : buttons) {
            if (button.getBounds().contains(mouseX, mouseY)) {

                buttons.forEach(b -> b.selected = (b == button));
                break;
            }
        }

        super.onMouseRelease(click);
    }

}
