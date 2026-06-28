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
import net.minecraft.client.input.MouseButtonEvent;
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
    Quad buttonQuad;

    private float inputHoverProgress = 0f;
    private float buttonHoverProgress = 0f;
    private long lastRenderTime = System.currentTimeMillis();

    public ChatTypingComponent(ChatTextComponent historyComponent) {
        this.historyComponent = historyComponent;
    }

    private void pushUndo() {
        undoStack.push(currentText.toString());
    }

    
    private Color interpolateColor(Color start, Color end, float progress) {
        float p = Math.max(0f, Math.min(1f, progress));
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * p);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * p);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * p);
        int a = (int) (start.getAlpha() + (end.getAlpha() - start.getAlpha()) * p);
        return new Color(r, g, b, a);
    }

    boolean chatOpen = false;

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

    public void render(Quad quad, int alpha, int mouseX, int mouseY) {

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastRenderTime) / 1000f;
        lastRenderTime = currentTime;


        float transitionSpeed = 3.5f;

        int buttonSpacing = 5;
        int buttonWidth = 28;

        int btnX = quad.getX() + quad.getWidth() - buttonWidth;
        int btnY = quad.getY();

        buttonQuad = new Quad(btnX, btnY, buttonWidth, quad.getHeight());

        int inputWidth = quad.getWidth() - buttonWidth - buttonSpacing;
        Quad textInputQuad = new Quad(quad.getX(), quad.getY(), inputWidth, quad.getHeight());

        Color base = new Color(ThemeManager.get(ThemeTarget.BORDER).getRGB(), true);

        float factor = 0.10f;

        int r = base.getRed()   + (int)((255 - base.getRed()) * factor);
        int g = base.getGreen() + (int)((255 - base.getGreen()) * factor);
        int b = base.getBlue()  + (int)((255 - base.getBlue()) * factor);

        Color border = new Color(r, g, b, alpha);


        boolean isInputHovered = textInputQuad.isHovered(mouseX, mouseY);
        if (isInputHovered) {
            inputHoverProgress = Math.min(1f, inputHoverProgress + deltaTime * transitionSpeed);
        } else {
            inputHoverProgress = Math.max(0f, inputHoverProgress - deltaTime * transitionSpeed);
        }

        Color inputBgNormal = new Color(17, 20, 32, alpha);
        Color inputBgHovered = new Color(17, 21, 38, alpha);
        Color inputBorderHovered = new Color(55, 56, 142, alpha);

        Color mixedInputBg = interpolateColor(inputBgNormal, inputBgHovered, inputHoverProgress);
        Color mixedInputBorder = interpolateColor(border, inputBorderHovered, inputHoverProgress);

        HybridRenderer2D.drawRoundRect(textInputQuad, mixedInputBg, mixedInputBorder, 8, 1);

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


        boolean isButtonHovered = buttonQuad.isHovered(mouseX, mouseY);
        if (isButtonHovered) {
            buttonHoverProgress = Math.min(1f, buttonHoverProgress + deltaTime * transitionSpeed);
        } else {
            buttonHoverProgress = Math.max(0f, buttonHoverProgress - deltaTime * transitionSpeed);
        }

        Color btnBgNormal = new Color(17, 20, 32, alpha);
        Color btnBgHovered = new Color(28, 30, 68, alpha);
        Color btnBorderHovered = new Color(61, 60, 168, alpha);

        Color mixedButtonBg = interpolateColor(btnBgNormal, btnBgHovered, buttonHoverProgress);
        Color mixedButtonBorder = interpolateColor(border, btnBorderHovered, buttonHoverProgress);

        HybridRenderer2D.drawRoundRect(buttonQuad, mixedButtonBg, mixedButtonBorder, 8, 1);

        HybridRenderText dots = HybridTextRenderer.getIconRenderer("dots", new Color(160,161,166,alpha));

        dots.setPosition(
                btnX + buttonWidth / 2 - dots.getWidth() / 2,
                btnY + buttonWidth / 2 - (dots.getHeight() + 6) / 2
        );

        HybridTextRenderer.addText(dots);
        HybridTextRenderer.render(RenderContext.get());
    }

    public void mouseRelease(MouseButtonEvent event) {
        if (buttonQuad.isHovered((int) event.x(), (int) event.y())) {
            chatOpen = !chatOpen;
            System.out.println("is chat open " + chatOpen);
        }
    }

    public boolean isChatOpen() {
        return chatOpen;
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
                    if (msg.startsWith("/")) {
                        mc.getConnection().sendCommand(msg.replace("/", ""));
                    } else{
                        mc.getConnection().sendChat(msg);
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

    public void appendToCurrentText(String text) {
        pushUndo();
        currentText.append(text);
        caretIndex = currentText.length();
        selectIndex = caretIndex;
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


}