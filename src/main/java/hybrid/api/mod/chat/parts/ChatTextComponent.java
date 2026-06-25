package hybrid.api.mod.chat.parts;

import hybrid.api.mod.chat.parts.msgs.MessageRenderFactory;
import hybrid.api.mod.chat.parts.msgs.RenderableMessage;
import hybrid.api.util.render.Quad;
import hybrid.api.util.texture.HybridTextureRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.ArrayList;
import java.util.List;

public class ChatTextComponent {

    public enum ChatType {
        TEXT,
        GIF
    }

    private final HybridTextureRenderer hybridTextureRenderer = new HybridTextureRenderer();
    private final List<ChatMessage> messageHistory = new ArrayList<>();
    private int messageIndexCounter = 0;

    public void render(Quad anchorQuad, Quad clipping, boolean playerHeads) {
        if (messageHistory.isEmpty()) return;

        float currentY = anchorQuad.getY() - 5;
        int renderedCount = 0;

        for (int i = messageHistory.size() - 1; i >= 0; i--) {
            if (renderedCount >= ChatLayoutController.getMaxVisibleMessages()) break;

            ChatMessage msg = messageHistory.get(i);
            boolean isGrouped = (i > 0) && messageHistory.get(i - 1).senderName().equals(msg.senderName());

            
            RenderableMessage block = MessageRenderFactory.create(msg, isGrouped, clipping, playerHeads);

            currentY -= ChatLayoutController.getLayoutMargin(isGrouped);
            currentY -= block.getHeight();

            block.render(hybridTextureRenderer, anchorQuad.getX(), currentY, clipping, ChatLayoutController.isChatHeads(), anchorQuad.height);

            renderedCount++;
        }
    }

    public void submitMessage(String name, Component message, PlayerSkin skin) {
        messageHistory.add(new ChatMessage(name, messageIndexCounter++, message, ChatType.TEXT, null, skin));
    }

    public void submitGif(String senderName, String path) {
        messageHistory.add(new ChatMessage(senderName, messageIndexCounter++, null, ChatType.GIF, path, null));
    }

    public List<ChatMessage> getMessageHistory() { return messageHistory; }

    public record ChatMessage(
            String senderName,
            int index,
            Component message,
            ChatType type,
            String gifPath,
            PlayerSkin skin
    ) {}
}