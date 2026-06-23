package hybrid.api.util.chat.parts;

import hybrid.api.Main;
import hybrid.api.util.render.ExternalImageRenderer;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ChatTextComponent {

    
    public enum ChatType {
        TEXT,
        GIF
    }

    
    public record ChatMessage(String senderName, int index, String message, ChatType type, String gifPath) {}

    private final List<ChatMessage> messageHistory = new ArrayList<>();
    private int messageIndexCounter = 0;

    private static final int MAX_VISIBLE_MESSAGES = 10;
    private static final float CHAT_FONT_SIZE = 11f;

    
    private static final int MESSAGE_SPACING = 14;
    private static final int GIF_RENDER_HEIGHT = 40;
    private static final int GIF_RENDER_WIDTH = 40;  

    public ChatTextComponent() {
    }
    public void render(Quad anchorQuad, Quad clipping) {
        if (messageHistory.isEmpty()) return;

        float currentY = anchorQuad.getY() - MESSAGE_SPACING;
        int renderedCount = 0;

        for (int i = messageHistory.size() - 1; i >= 0; i--) {
            if (renderedCount >= MAX_VISIBLE_MESSAGES) break;

            ChatMessage msg = messageHistory.get(i);

            if (msg.type() == ChatType.TEXT) {
                
                String formattedMessage = msg.senderName() + ": " + msg.message();

                Main.RENDERER.drawText(
                        Main.getStyle(),
                        formattedMessage,
                        anchorQuad.getX() + 6,
                        currentY,
                        CHAT_FONT_SIZE,
                        -1,
                        clipping.copy().subtractHeight(anchorQuad.height + 8)
                );

                currentY -= MESSAGE_SPACING;
            } else if (msg.type() == ChatType.GIF) {
                String senderPrefix = msg.senderName();
                Main.RENDERER.drawText(
                        Main.getStyle(),
                        senderPrefix,
                        anchorQuad.getX() + 6,
                        currentY,
                        CHAT_FONT_SIZE,
                        -1,
                        clipping.copy().subtractHeight(anchorQuad.height + 8)
                );

                
                currentY -= MESSAGE_SPACING;

                
                float gifCenterX = anchorQuad.getX() + ((anchorQuad.getWidth() - GIF_RENDER_WIDTH) / 2f);

                
                float gifYPos = currentY - GIF_RENDER_HEIGHT + 10;

                Path path = Paths.get(msg.gifPath());
                ExternalImageRenderer.renderGif(RenderContext.get(), path, gifCenterX, gifYPos, GIF_RENDER_WIDTH, GIF_RENDER_HEIGHT);

                currentY -= (GIF_RENDER_HEIGHT);
            }

            renderedCount++;
        }
    }

    
    public void addMessage(String senderName, String message) {
        messageHistory.add(new ChatMessage(senderName, messageIndexCounter++, message, ChatType.TEXT, null));
    }

    
    public void submitGif(String senderName, String path) {
        messageHistory.add(new ChatMessage(senderName, messageIndexCounter++, "[GIF]", ChatType.GIF, path));
    }

    public List<ChatMessage> getMessageHistory() {
        return messageHistory;
    }
}