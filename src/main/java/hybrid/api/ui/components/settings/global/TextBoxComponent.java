package hybrid.api.ui.components.settings.global;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.components.HybridComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

import static hybrid.api.HybridApi.mc;

public class TextBoxComponent extends HybridComponent {

    String text = "";

    int selectionStart = -1, selectionEnd = -1, caretIndex = 0, scrollOffset = 0, dragStartIndex = -1;
    long lastClickTime = 0, lastBlink = 0;
    int clickCount = 0;
    boolean caretVisible = true, dragging = false;

    public String getText() {
        return text;
    }

    @Override
    public void render(HybridRenderer hybridRenderer) {

        caretIndex = Math.max(0, Math.min(caretIndex, text.length()));

        if (selectionStart != -1) selectionStart = Math.max(0, Math.min(selectionStart, text.length()));

        if (selectionEnd != -1) selectionEnd = Math.max(0, Math.min(selectionEnd, text.length()));

        hybridRenderer.drawOutlineQuad(componentBounds, HybridThemeMap.get(ThemeColorKey.modBackgroundColor), HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor), 4, 1);

        HybridRenderText text = HybridTextRenderer.getTextRenderer(this.text, FontStyle.BOLD, 20, Color.WHITE, Color.GRAY, true);

        int textY = componentBounds.getY() + (componentBounds.getHeight() - text.getHeight()) / 2;
        int textX = componentBounds.getX() + 6 - scrollOffset;

        text.setPosition(textX, textY);

        int caretPixel = HybridTextRenderer.getStringWidth(this.text.substring(0, caretIndex), FontStyle.BOLD, 20);

        int visibleWidth = componentBounds.getWidth() - 12;

        if (caretPixel - scrollOffset > visibleWidth) {
            scrollOffset = caretPixel - visibleWidth + 10;
        }

        if (caretPixel - scrollOffset < 0) {
            scrollOffset = caretPixel;
        }

        scrollOffset = Math.max(0, scrollOffset);

        HybridRenderer.CONTEXT_LIST.add((ctx, renderer) -> {

            ctx.enableScissor(componentBounds.getX(), componentBounds.getY(), componentBounds.getX() + componentBounds.getWidth() - 1, componentBounds.getY() + componentBounds.getHeight());

            if (selectionStart != -1 && selectionEnd != -1) {

                int start = Math.min(selectionStart, selectionEnd);
                int end = Math.max(selectionStart, selectionEnd);

                int startX = text.getX() + HybridTextRenderer.getStringWidth(this.text.substring(0, start), FontStyle.BOLD, 20);

                int endX = text.getX() + HybridTextRenderer.getStringWidth(this.text.substring(0, end), FontStyle.BOLD, 20);

                ctx.fill(startX, text.getY(), endX, text.getY() + text.getHeight(), new Color(88, 57, 255, 100).getRGB());
            }

            text.draw(ctx);

            long now = System.currentTimeMillis();

            if (now - lastBlink > 500) {
                caretVisible = !caretVisible;
                lastBlink = now;
            }

            if (caretVisible) {

                int caretX = text.getX() + HybridTextRenderer.getStringWidth(this.text.substring(0, caretIndex), FontStyle.BOLD, 20);

                ctx.fill(caretX, text.getY(), caretX + 1, text.getY() + text.getHeight(), Color.WHITE.getRGB());
            }

            ctx.disableScissor();
        });


        super.render(hybridRenderer);
    }

    @Override
    public void onMouseClicked(Click click) {

        long now = System.currentTimeMillis();

        if (now - lastClickTime < 250) {
            clickCount++;
        } else {
            clickCount = 1;
        }

        int index = getCharIndexFromMouse((int) click.x());

        if (clickCount == 1) {

            selectionStart = index;
            selectionEnd = index;

            caretIndex = index;

            dragging = true;
            dragStartIndex = index;
        } else if (clickCount == 2) {
            selectWordAt((int) click.x());
            caretIndex = selectionEnd;
        } else if (clickCount >= 3) {
            selectionStart = 0;
            selectionEnd = text.length();
            caretIndex = selectionEnd;
        }

        lastClickTime = now;

        super.onMouseClicked(click);
    }

    @Override
    public void onMouseDrag(Click click) {
        if (!dragging) return;

        int index = getCharIndexFromMouse((int) click.x());

        selectionStart = dragStartIndex;
        selectionEnd = index;

        caretIndex = index;

        super.onMouseDrag(click);
    }


    @Override
    public void onMouseRelease(Click click) {
        dragging = false;
        super.onMouseRelease(click);
    }


    @Override
    public void onCharTyped(CharInput input) {

        if (input.isValidChar()) {

            if (selectionStart != -1) {
                deleteSelection();
            }

            char c = (char) input.codepoint();

            text = text.substring(0, caretIndex) + c + text.substring(caretIndex);

            caretIndex++;
        }

        super.onCharTyped(input);
    }

    @Override
    public void keyPressed(KeyInput input) {

        boolean shift = (input.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0;

        if (input.getKeycode() == GLFW.GLFW_KEY_LEFT) {

            if (caretIndex > 0) {
                caretIndex--;
            }

            if (!shift) {
                selectionStart = -1;
                selectionEnd = -1;
            } else {
                if (selectionStart == -1) selectionStart = caretIndex + 1;
                selectionEnd = caretIndex;
            }
        }

        if (input.getKeycode() == GLFW.GLFW_KEY_RIGHT) {

            if (caretIndex < text.length()) {
                caretIndex++;
            }

            if (!shift) {
                selectionStart = -1;
                selectionEnd = -1;
            } else {
                if (selectionStart == -1) selectionStart = caretIndex - 1;
                selectionEnd = caretIndex;
            }
        }

        boolean ctrl = (input.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0;

        if (ctrl) {

            if (input.getKeycode() == GLFW.GLFW_KEY_A) {
                selectionStart = 0;
                selectionEnd = text.length();
                caretIndex = selectionEnd;
            }

            if (input.getKeycode() == GLFW.GLFW_KEY_C) {

                if (selectionStart != -1 && selectionEnd != -1) {

                    int start = Math.min(selectionStart, selectionEnd);
                    int end = Math.max(selectionStart, selectionEnd);

                    String selected = text.substring(start, end);

                    mc.keyboard.setClipboard(selected);
                }
            }

            if (input.getKeycode() == GLFW.GLFW_KEY_V) {

                String clip = mc.keyboard.getClipboard();

                if (selectionStart != -1) {
                    deleteSelection();
                }

                text = text.substring(0, caretIndex) + clip + text.substring(caretIndex);

                caretIndex += clip.length();
            }

            if (input.getKeycode() == GLFW.GLFW_KEY_X) {

                if (selectionStart != -1 && selectionEnd != -1) {

                    int start = Math.min(selectionStart, selectionEnd);
                    int end = Math.max(selectionStart, selectionEnd);

                    String selected = text.substring(start, end);

                    MinecraftClient.getInstance().keyboard.setClipboard(selected);

                    deleteSelection();
                }
            }
        }

        if (input.getKeycode() == GLFW.GLFW_KEY_BACKSPACE) {

            if (selectionStart != -1) {
                deleteSelection();
                return;
            }

            if (caretIndex > 0) {

                text = text.substring(0, caretIndex - 1) + text.substring(caretIndex);

                caretIndex--;
            }
        }

        if (input.getKeycode() == GLFW.GLFW_KEY_DELETE) {

            if (selectionStart != -1) {
                deleteSelection();
                return;
            }

            if (caretIndex < text.length()) {

                text = text.substring(0, caretIndex) + text.substring(caretIndex + 1);
            }
        }

        super.keyPressed(input);
    }

    private void deleteSelection() {

        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);

        text = text.substring(0, start) + text.substring(end);

        caretIndex = start;

        selectionStart = -1;
        selectionEnd = -1;
    }

    private void selectWordAt(int mouseX) {

        int index = getCharIndexFromMouse(mouseX);

        if (index < 0 || index >= text.length()) return;

        int start = index;
        int end = index;

        while (start > 0 && text.charAt(start - 1) != ' ') start--;
        while (end < text.length() && text.charAt(end) != ' ') end++;

        selectionStart = start;
        selectionEnd = end;
    }

    private int getCharIndexFromMouse(int mouseX) {

        int relativeX = mouseX - (componentBounds.getX() + 6) + scrollOffset;

        int width = 0;

        for (int i = 0; i < text.length(); i++) {

            int charWidth = HybridTextRenderer.getCharWidth(text.charAt(i), FontStyle.BOLD, 20);

            if (width + charWidth >= relativeX) {
                return i;
            }

            width += charWidth;
        }

        return text.length();
    }
}