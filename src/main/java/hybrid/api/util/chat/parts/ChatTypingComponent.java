package hybrid.api.util.chat.parts;

import hybrid.api.Main;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChatTypingComponent {

    // Define the record to hold message details
    public record ChatMessage(String senderName, int index, String message) {}

    private final StringBuilder currentText = new StringBuilder();
    private Quad textInputQuad;

    // Track sent messages and assign unique indices
    private final List<ChatMessage> messageHistory = new ArrayList<>();
    private int messageIndexCounter = 0;

    public ChatTypingComponent() {
    }

    public void render(Quad quad) {
        int buttonSpacing = 5;
        int buttonWidth = 28;

        int btnX = quad.getX() + quad.getWidth() - buttonWidth;
        int btnY = quad.getY();
        int btnH = quad.getHeight();
        Quad buttonQuad = new Quad(btnX, btnY, buttonWidth, btnH);

        int inputWidth = quad.getWidth() - buttonWidth - buttonSpacing;
        textInputQuad = new Quad(quad.getX(), quad.getY(), inputWidth, quad.getHeight());

        HybridRenderer2D.drawRoundRect(textInputQuad, new Color(101, 98, 98, 124), Color.RED, 8, 0);

        String textToDraw = currentText.toString();

        if (System.currentTimeMillis() % 1000 < 500) {
            textToDraw += "|";
        } else {
            textToDraw += " ";
        }

        float fontSize = 11f;
        float textY = textInputQuad.getY() + ((float) textInputQuad.getHeight() / 2f) - (fontSize / 2f);

        Quad clippy = textInputQuad.copy().addX(5).subtractWidth(10);

        Main.RENDERER.drawText(
                Main.getStyle(),
                textToDraw,
                textInputQuad.getX() + 6,
                textY,
                fontSize,
                -1,clippy
        );




        HybridRenderer2D.drawRoundRect(buttonQuad, new Color(255, 255, 255, 140), Color.RED, 8, 0);
        HybridRenderText dots = HybridTextRenderer.getIconRenderer("dots", Color.BLACK);
        dots.setPosition(btnX + buttonWidth / 2 - dots.getWidth() / 2, btnY + buttonWidth / 2 - (dots.getHeight() + 6) / 2);
        HybridTextRenderer.addText(dots);
        HybridTextRenderer.render(RenderContext.get());
    }

    public void charTyped(CharacterEvent characterEvent) {
        char codePoint = (char) characterEvent.codepoint();

        if (codePoint >= 32 && codePoint != 127) {
            currentText.append(codePoint);
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        // Handle Backspace
        if (keyEvent.key() == GLFW.GLFW_KEY_BACKSPACE) {
            if (!currentText.isEmpty()) {
                currentText.deleteCharAt(currentText.length() - 1);
            }
        }

        // Handle Enter key to submit the message
        if (keyEvent.key() == GLFW.GLFW_KEY_ENTER || keyEvent.key() == GLFW.GLFW_KEY_KP_ENTER) {
            String messageToSend = currentText.toString().trim();

            if (!messageToSend.isEmpty()) {
                // Instantiating the record with (Name, index, message)
                // Swap "LocalPlayer" out with Minecraft.getInstance().getUser().getName() if desired!
                ChatMessage newMessage = new ChatMessage("LocalPlayer", messageIndexCounter++, messageToSend);

                messageHistory.add(newMessage);

                clearText();
            }
        }
    }

    // Grab the full list of sent messages from other classes
    public List<ChatMessage> getMessageHistory() {
        return messageHistory;
    }

    public String getText() {
        return currentText.toString();
    }

    public void clearText() {
        currentText.setLength(0);
    }
}