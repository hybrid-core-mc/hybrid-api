package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.settings.TextListSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.HybridScreen;
import hybrid.api.ui.components.screen.ModHybridComponent;
import hybrid.api.ui.components.settings.global.TextBoxComponent;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static hybrid.api.HybridApi.mc;

public class TextListComponent extends SettingComponent {

    private final TextListSetting setting;
    private final TextBoxComponent textBoxComponent;

    private final List<ScreenBounds> itemBounds = new ArrayList<>();
    private final List<Integer> itemIndexes = new ArrayList<>();

    private ScreenBounds addBounds;
    private ScreenBounds textListBounds;

    private boolean deleting;
    private float scrollOffset = 0;

    public TextListComponent(TextListSetting setting) {
        this.setting = setting;
        this.textBoxComponent = new TextBoxComponent();
        deleting = false;
        setHeight(80);
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
        int boxY = componentBounds.getY() + 4;

        int addWidth = 20;
        int spacing = 3;

        int textBoxX = componentBounds.getX() + componentBounds.getWidth() - boxWidth;
        int addX = textBoxX - addWidth - spacing;

        addBounds = new ScreenBounds(addX, boxY, addWidth, boxHeight);

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

        HybridRenderText plus = !deleting
                ? HybridTextRenderer.getTextRenderer("+", FontStyle.BOLD, 20, Color.WHITE)
                : HybridTextRenderer.getIconRenderer("trash", Color.WHITE);

        int plusX = addBounds.getX() + (addBounds.getWidth() - plus.getWidth()) / 2;
        int plusY = addBounds.getY() + (addBounds.getHeight() - plus.getHeight()) / 2;

        plus.setPosition(plusX, plusY);
        HybridTextRenderer.addText(plus);

        textBoxComponent.render(hybridRenderer);

        int labelY = boxY + (boxHeight - label.getHeight()) / 2;
        label.setPosition(componentBounds.getX(), labelY);
        HybridTextRenderer.addText(label);

        int headingBottom = boxY + boxHeight;

        int padding = 5;
        int textX = componentBounds.getX() + padding;

        int maxTextHeight = 0;

        for (String s : setting.get()) {
            HybridRenderText t = HybridTextRenderer.getTextRenderer(
                    s,
                    FontStyle.BOLD,
                    15,
                    Color.WHITE
            );
            maxTextHeight = Math.max(maxTextHeight, t.getHeight());
        }

        int rowHeight = Math.max(1, maxTextHeight + 3);

        if (setting.get().isEmpty()) {
            int headerHeight = boxHeight + 10;
            setHeight(headerHeight);
            super.render(hybridRenderer);
            return;
        }

        int contentHeight = setting.get().size() * rowHeight + padding * 2;

        int maxHeight = 75;
        int listHeight = Math.min(contentHeight, maxHeight);

        int headerHeight = boxHeight + 10;

        setHeight(headerHeight + listHeight + 10);

        int listY = headingBottom + 6;

        textListBounds = new ScreenBounds(
                componentBounds.getX(),
                listY,
                componentBounds.getWidth(),
                listHeight
        );

        hybridRenderer.drawOutlineQuad(
                textListBounds,
                HybridThemeMap.get(ThemeColorKey.modsBackgroundColor),
                HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                4,
                1
        );

        int totalContentHeight = setting.get().size() * rowHeight;

        int maxScroll = Math.max(0, totalContentHeight - textListBounds.getHeight());

        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;

        itemBounds.clear();
        itemIndexes.clear();

        int layoutY = (int) (textListBounds.getY() + padding - scrollOffset);

        for (int index = 0; index < setting.get().size(); index++) {

            String s = setting.get().get(index);

            HybridRenderText text = HybridTextRenderer.getTextRenderer(
                    s,
                    FontStyle.BOLD,
                    15,
                    Color.WHITE
            );

            int centeredY = layoutY + (rowHeight - text.getHeight()) / 2;

            ScreenBounds bounds = new ScreenBounds(
                    textX - 4,
                    centeredY - 2,
                    text.getWidth() + 8,
                    text.getHeight() + 4
            );

            if (bounds.getY() + bounds.getHeight() >= textListBounds.getY()
                    && bounds.getY() <= textListBounds.getY() + textListBounds.getHeight()) {

                itemBounds.add(bounds);
                itemIndexes.add(index);
            }

            layoutY += rowHeight;
        }

        final int finalTextX = textX;
        final int finalPadding = padding;
        final int finalRowHeight = rowHeight;

        HybridRenderer.CONTEXT_LIST.add((c, j) -> {

            c.enableScissor(
                    textListBounds.getX(),
                    textListBounds.getY(),
                    textListBounds.getX() + textListBounds.getWidth(),
                    textListBounds.getY() + textListBounds.getHeight()
            );

            int drawY = (int) (textListBounds.getY() + finalPadding - scrollOffset);

            for (int index = 0; index < setting.get().size(); index++) {

                String s = setting.get().get(index);

                HybridRenderText text = HybridTextRenderer.getTextRenderer(
                        s,
                        FontStyle.BOLD,
                        15,
                        Color.WHITE
                );

                ScreenBounds bounds = new ScreenBounds(
                        finalTextX - 8,
                        drawY - 2,
                        text.getWidth() + 8,
                        text.getHeight() + 4
                );

                if (bounds.getY() + bounds.getHeight() >= textListBounds.getY()
                        && bounds.getY() <= textListBounds.getY() + textListBounds.getHeight()) {

                    int textY = drawY + (finalRowHeight - text.getHeight()) / 2;

                    text.setPosition(finalTextX, textY);
                    text.draw(c);
                }

                drawY += finalRowHeight;
            }

            c.disableScissor();
        });

        super.render(hybridRenderer);
    }


    @Override
    public void onMouseClicked(Click click) {
        textBoxComponent.onMouseClicked(click);
        super.onMouseClicked(click);
    }

    @Override
    public void onMouseDrag(Click click) {
        textBoxComponent.onMouseDrag(click);
        super.onMouseDrag(click);
    }

    @Override
    public void onMouseRelease(Click click) {

        if (addBounds != null && addBounds.contains(click.x(), click.y())) {

            if (click.button() == 0 && !deleting) {
                addText();
            }

            if (click.button() == 1) {
                deleting = !deleting;
            }
        }

        if (click.button() == 0) {

            if (textListBounds != null && textListBounds.contains(click.x(), click.y())) {

                for (int i = 0; i < itemBounds.size(); i++) {

                    ScreenBounds bounds = itemBounds.get(i);

                    if (bounds.contains(click.x(), click.y())) {

                        if (deleting) {

                            int removeIndex = itemIndexes.get(i);

                            List<String> newList = new ArrayList<>(setting.get());

                            if (removeIndex >= 0 && removeIndex < newList.size()) {

                                newList.remove(removeIndex);
                                setting.set(newList);
                            }
                        }

                        break;
                    }
                }
            }
        }

        textBoxComponent.onMouseRelease(click);

        super.onMouseRelease(click);
    }

    @Override
    public void onMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

        if (textListBounds != null && textListBounds.contains(mouseX, mouseY)) {

            float scrollSpeed = 20f;

            scrollOffset -= (float) (verticalAmount * scrollSpeed);

            if (scrollOffset < 0) scrollOffset = 0;
        }

        if (!(mc.currentScreen instanceof HybridScreen screen)) return;

        for (var component : screen.getHybridModComponentList()) {

            if (component instanceof ModHybridComponent modHybridComponent) {

                System.out.println("can it ignore " + (textListBounds != null && textListBounds.contains(mouseX, mouseY)));
                modHybridComponent.ignoreScroll = textListBounds != null && textListBounds.contains(mouseX, mouseY);

                break;
            }
        }

        super.onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void onCharTyped(CharInput input) {
        textBoxComponent.onCharTyped(input);
        super.onCharTyped(input);
    }

    @Override
    public void keyPressed(KeyInput input) {

        textBoxComponent.keyPressed(input);

        if (input.key() == GLFW.GLFW_KEY_ENTER && !deleting) {
            addText();
        }

        super.keyPressed(input);
    }

    public void addText() {

        if (!textBoxComponent.getText().isEmpty()) {

            List<String> newList = new ArrayList<>(setting.get());

            newList.add(textBoxComponent.getText());

            setting.set(newList);

            textBoxComponent.setText("");
        }
    }
}
