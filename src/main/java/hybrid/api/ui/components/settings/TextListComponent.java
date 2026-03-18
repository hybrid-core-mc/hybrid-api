package hybrid.api.ui.components.settings;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mod.settings.TextListSetting;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.HybridScreen;
import hybrid.api.ui.animation.PositionAnimation;
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

    private final int PADDING = 5;
    private float scrollOffset = 0f;
    private float maxScroll = 0f;

    private final PositionAnimation addButtonAnim = new PositionAnimation(0, 0.3f);
    private final PositionAnimation textBoxAnim = new PositionAnimation(0, 0.7f);

    public TextListComponent(TextListSetting setting) {
        this.setting = setting;
        this.textBoxComponent = new TextBoxComponent();
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

        int normalTextX = componentBounds.getX() + componentBounds.getWidth() - boxWidth;
        int normalAddX = normalTextX - addWidth - spacing;

        int deleteAddX = componentBounds.getX() + componentBounds.getWidth() - addWidth;


        if (deleting) {
            addButtonAnim.setTarget(deleteAddX);
            textBoxAnim.setTarget(componentBounds.getX() + componentBounds.getWidth() + 10);
        } else {
            addButtonAnim.setTarget(normalAddX);
            textBoxAnim.setTarget(normalTextX);
        }

        addButtonAnim.update();
        textBoxAnim.update();

        int animatedAddX = (int) addButtonAnim.get();
        int animatedTextX = (int) textBoxAnim.get();

        addBounds = new ScreenBounds(animatedAddX, boxY, addWidth, boxHeight);

        textBoxComponent.componentBounds = new ScreenBounds(
                animatedTextX,
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

        plus.setPosition(
                addBounds.getX() + (addBounds.getWidth() - plus.getWidth()) / 2,
                addBounds.getY() + (addBounds.getHeight() - plus.getHeight()) / 2
        );

        HybridTextRenderer.addText(plus);

        if (!deleting) {
            textBoxComponent.render(hybridRenderer);
        }

        label.setPosition(
                componentBounds.getX(),
                boxY + (boxHeight - label.getHeight()) / 2
        );

        HybridTextRenderer.addText(label);

        int headingBottom = boxY + boxHeight;

        if (setting.get().isEmpty()) {
            setHeight(boxHeight + 10);
            super.render(hybridRenderer);
            return;
        }

        int maxTextHeight = 0;

        for (String s : setting.get()) {
            HybridRenderText t = HybridTextRenderer.getTextRenderer(
                    s, FontStyle.BOLD, 15, Color.WHITE
            );
            maxTextHeight = Math.max(maxTextHeight, t.getHeight());
        }

        int rowHeight = maxTextHeight + 4;

        int contentHeight = setting.get().size() * rowHeight;

        int maxListHeight = 75;
        int listHeight = Math.min(contentHeight + PADDING * 2, maxListHeight);

        int listY = headingBottom + 6;

        textListBounds = new ScreenBounds(
                componentBounds.getX(),
                listY,
                componentBounds.getWidth(),
                listHeight
        );

        maxScroll = Math.max(0, contentHeight - listHeight + PADDING * 2);

        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        hybridRenderer.drawOutlineQuad(
                textListBounds,
                HybridThemeMap.get(ThemeColorKey.modsBackgroundColor),
                HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                4,
                1
        );

        itemBounds.clear();
        itemIndexes.clear();

        int drawY = (int) (textListBounds.getY() + PADDING - scrollOffset);
        int textX = componentBounds.getX() + PADDING;

        for (int i = 0; i < setting.get().size(); i++) {

            String s = setting.get().get(i);

            HybridRenderText text = HybridTextRenderer.getTextRenderer(
                    s, FontStyle.BOLD, 15, Color.WHITE
            );

            int textY = drawY + (rowHeight - text.getHeight()) / 2;

            ScreenBounds bounds = new ScreenBounds(
                    textX - 4,
                    textY - 2,
                    text.getWidth() + 8,
                    text.getHeight() + 4
            );

            if (bounds.getY() + bounds.getHeight() >= textListBounds.getY() &&
                    bounds.getY() <= textListBounds.getY() + textListBounds.getHeight()) {

                itemBounds.add(bounds);
                itemIndexes.add(i);
            }

            drawY += rowHeight;
        }

        final int finalRowHeight = rowHeight;
        final int finalTextX = textX;

        HybridRenderer.CONTEXT_LIST.add((c, j) -> {

            HybridScreen screen = (HybridScreen) mc.currentScreen;
            ScreenBounds screenBounds = screen.getBounds();

            int x1 = Math.max(textListBounds.getX(), screenBounds.getX());
            int y1 = Math.max(textListBounds.getY(), screenBounds.getY());
            int x2 = Math.min(textListBounds.getX() + textListBounds.getWidth(),
                    screenBounds.getX() + screenBounds.getWidth());
            int y2 = Math.min(textListBounds.getY() + textListBounds.getHeight(),
                    screenBounds.getY() + screenBounds.getHeight());

            c.enableScissor(x1, y1, x2, y2);

            int y = (int) (textListBounds.getY() + PADDING - scrollOffset);

            for (String s : setting.get()) {

                HybridRenderText text = HybridTextRenderer.getTextRenderer(
                        s, FontStyle.BOLD, 15, Color.WHITE
                );

                int textY = y + (finalRowHeight - text.getHeight()) / 2;

                text.setPosition(finalTextX, textY);
                text.draw(c);

                y += finalRowHeight;
            }

            c.disableScissor();
        });

        setHeight(boxHeight + 10 + listHeight + 10);

        super.render(hybridRenderer);
    }

    @Override
    public void onMouseScroll(double mouseX, double mouseY, double horizontal, double vertical) {

        if (textListBounds != null && textListBounds.contains(mouseX, mouseY)) {

            float scrollSpeed = 18f;
            scrollOffset -= (float) (vertical * scrollSpeed);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

            if (mc.currentScreen instanceof HybridScreen screen) {
                for (var component : screen.getHybridModComponentList()) {
                    if (component instanceof ModHybridComponent mod) {
                        mod.ignoreScroll = true;
                        break;
                    }
                }
            }
        }

        super.onMouseScroll(mouseX, mouseY, horizontal, vertical);
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

            if (click.button() == 0 && !deleting)
                addText();

            if (click.button() == 1)
                deleting = !deleting;
        }

        if (click.button() == 0 &&
                textListBounds != null &&
                textListBounds.contains(click.x(), click.y())) {

            for (int i = 0; i < itemBounds.size(); i++) {

                if (itemBounds.get(i).contains(click.x(), click.y())) {

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

        textBoxComponent.onMouseRelease(click);
        super.onMouseRelease(click);
    }

    @Override
    public void onCharTyped(CharInput input) {
        textBoxComponent.onCharTyped(input);
        super.onCharTyped(input);
    }

    @Override
    public void keyPressed(KeyInput input) {

        textBoxComponent.keyPressed(input);

        if (input.key() == GLFW.GLFW_KEY_ENTER && !deleting)
            addText();

        super.keyPressed(input);
    }

    private void addText() {

        if (textBoxComponent.getText().isEmpty()) return;

        List<String> newList = new ArrayList<>(setting.get());

        newList.add(textBoxComponent.getText());

        setting.set(newList);

        textBoxComponent.setText("");
    }
}