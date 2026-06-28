package hybrid.api.mod.chat.parts;

import hybrid.api.Main;
import hybrid.api.theme.ThemeManager;
import hybrid.api.theme.ThemeTarget;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import hybrid.api.util.texture.PlayerInfoAccessor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static hybrid.api.Main.mc;

public class ChatTypingComponent {

    private final StringBuilder currentText = new StringBuilder();
    private final ChatTextComponent historyComponent;

    private static final float FONT_SIZE = 11f;

    private int caretIndex = 0;
    private int selectIndex = 0;
    private boolean selecting = false;

    private final Deque<String> undoStack = new ArrayDeque<>();

    public ChatTypingComponent(ChatTextComponent historyComponent) {
        this.historyComponent = historyComponent;
    }

    private void pushUndo() {
        undoStack.push(currentText.toString());
    }

    public void render(Quad quad,int alpha) {
        int buttonSpacing = 5;
        int buttonWidth = 28;

        int btnX = quad.getX() + quad.getWidth() - buttonWidth;
        int btnY = quad.getY();

        Quad buttonQuad = new Quad(btnX, btnY, buttonWidth, quad.getHeight());

        int inputWidth = quad.getWidth() - buttonWidth - buttonSpacing;
        Quad textInputQuad = new Quad(quad.getX(), quad.getY(), inputWidth, quad.getHeight());


        Color base = new Color(ThemeManager.get(ThemeTarget.BORDER).getRGB(), true);

        float factor = 0.10f;

        int r = base.getRed()   + (int)((255 - base.getRed()) * factor);
        int g = base.getGreen() + (int)((255 - base.getGreen()) * factor);
        int b = base.getBlue()  + (int)((255 - base.getBlue()) * factor);

        Color border = new Color(r, g, b, alpha);

        HybridRenderer2D.drawRoundRect(textInputQuad, new Color(17, 20, 32, alpha),border
                , 8, 1);

        String fullText = currentText.toString();

        int start = Math.min(caretIndex, selectIndex);
        int end = Math.max(caretIndex, selectIndex);

        float padding = 6f;

        String before = fullText.substring(0, start);
        String selected = fullText.substring(start, end);

        float x = textInputQuad.getX() + padding;
        float y = textInputQuad.getY() + (textInputQuad.getHeight() / 2f) - (FONT_SIZE / 2f);

        float visibleWidth = textInputQuad.getWidth() - padding * 2;

        float beforeWidth = Main.getStyle().getWidth(before, FONT_SIZE);
        float selectedWidth = Main.getStyle().getWidth(selected, FONT_SIZE);
        float totalWidth = Main.getStyle().getWidth(fullText, FONT_SIZE);

        if (totalWidth > visibleWidth) {
            x -= (totalWidth - visibleWidth);
        }

        float selectionX = x + beforeWidth;

        if (selecting && caretIndex != selectIndex) {
            HybridRenderer2D.drawRoundRect(
                    new Quad((int) selectionX, textInputQuad.getY(), (int) selectedWidth, (int) ChatLayoutController.getChatFontSize()),
                    new Color(28, 73, 140, alpha),
                    Color.BLACK,
                    0,
                    0
            );
        }

        String display = fullText;
        if (System.currentTimeMillis() % 1000 < 500) {
            display = fullText.substring(0, caretIndex) + "|" + fullText.substring(caretIndex);
        }

        Main.RENDERER.drawText(
                Main.getStyle(),
                display,
                x,
                y,
                FONT_SIZE,
                new Color(255, 255, 255,alpha).getRGB(),
                0,
                textInputQuad
        );

        HybridRenderer2D.drawRoundRect(buttonQuad, new Color(17, 20, 32, alpha), border, 8, 1);

        HybridRenderText dots = HybridTextRenderer.getIconRenderer("dots", new Color(160,161,166,alpha));
        dots.setPosition(
                btnX + buttonWidth / 2 - dots.getWidth() / 2,
                btnY + buttonWidth / 2 - (dots.getHeight() + 6) / 2
        );

        HybridTextRenderer.addText(dots);
        HybridTextRenderer.render(RenderContext.get());
    }

    public void charTyped(CharacterEvent e) {
        selecting = false;

        char c = (char) e.codepoint();
        if (c >= 32 && c != 127) {
            pushUndo();
            deleteSelectionIfAny();
            currentText.insert(caretIndex, c);
            caretIndex++;
            selectIndex = caretIndex;
        }
    }

    public void keyPressed(KeyEvent e) {

        boolean ctrl = e.hasControlDown();
        boolean shift = e.hasShiftDown();

        switch (e.key()) {

            case GLFW.GLFW_KEY_A -> {
                if (ctrl) {
                    caretIndex = currentText.length();
                    selectIndex = 0;
                    selecting = true;
                }
            }

            case GLFW.GLFW_KEY_C -> {
                if (ctrl) {
                    String selected = getSelectedText();
                    if (!selected.isEmpty()) {
                        mc.keyboardHandler.setClipboard(selected);
                    }
                }
            }

            case GLFW.GLFW_KEY_X -> {
                if (ctrl) {
                    String selected = getSelectedText();
                    if (!selected.isEmpty()) {
                        mc.keyboardHandler.setClipboard(selected);
                        pushUndo();
                        deleteSelectionIfAny();
                    }
                }
            }

            case GLFW.GLFW_KEY_V -> {
                if (ctrl) {
                    pushUndo();
                    deleteSelectionIfAny();

                    String paste = mc.keyboardHandler.getClipboard();
                    currentText.insert(caretIndex, paste);

                    caretIndex += paste.length();
                    selectIndex = caretIndex;
                }
            }

            case GLFW.GLFW_KEY_Z -> {
                if (ctrl) {
                    if (!undoStack.isEmpty()) {
                        String prev = undoStack.pop();
                        currentText.setLength(0);
                        currentText.append(prev);

                        caretIndex = currentText.length();
                        selectIndex = caretIndex;
                    }
                }
            }

            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (hasSelection()) {
                    pushUndo();
                    deleteSelectionIfAny();
                } else if (caretIndex > 0) {
                    pushUndo();
                    currentText.deleteCharAt(caretIndex - 1);
                    caretIndex--;
                    selectIndex = caretIndex;
                }
            }

            case GLFW.GLFW_KEY_LEFT -> {
                if (caretIndex > 0) caretIndex--;
                if (!shift) selectIndex = caretIndex;
                selecting = shift;
            }

            case GLFW.GLFW_KEY_RIGHT -> {
                if (caretIndex < currentText.length()) caretIndex++;
                if (!shift) selectIndex = caretIndex;
                selecting = shift;
            }

            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
                String msg = currentText.toString().trim();

                if (!msg.isEmpty()) {

                    List<PlayerInfo> playerInfos =
                            ((PlayerInfoAccessor) mc.gui.getTabList()).hybrid_api$playerInfo();

                    PlayerInfo info = null;
                    for (PlayerInfo p : playerInfos) {
                        if (p.getProfile().equals(mc.player.getGameProfile())) {
                            info = p;
                        }
                    }

                    if (info != null) {
                        historyComponent.submitMessage(
                                mc.player.getGameProfile().name(),
                                Component.literal(msg),
                                mc.player.getSkin()
                        );
                    }

                    clearText();
                }
            }
        }
    }

    private boolean hasSelection() {
        return caretIndex != selectIndex;
    }

    private String getSelectedText() {
        int start = Math.min(caretIndex, selectIndex);
        int end = Math.max(caretIndex, selectIndex);
        return currentText.substring(start, end);
    }

    private void deleteSelectionIfAny() {
        if (!hasSelection()) return;

        int start = Math.min(caretIndex, selectIndex);
        int end = Math.max(caretIndex, selectIndex);

        currentText.delete(start, end);
        caretIndex = start;
        selectIndex = start;
    }

    public void clearText() {
        currentText.setLength(0);
        caretIndex = 0;
        selectIndex = 0;
        undoStack.clear();
    }

    public String getText() {
        return currentText.toString();
    }

    public void keyReleased(KeyEvent keyEvent) {
    }
}