package hybrid.api.mod.chat.parts.msgs;

import hybrid.api.mod.chat.parts.ChatTextComponent;
import hybrid.api.util.render.Quad;

public class MessageRenderFactory {
    public static RenderableMessage create(ChatTextComponent.ChatMessage msg, boolean isGrouped, Quad clipping, boolean playerHeads) {
        if (msg.type() == ChatTextComponent.ChatType.GIF) {
            return new ImageRenderableMessage(msg, isGrouped, clipping, playerHeads);
        }
        return new TextRenderableMessage(msg, isGrouped, clipping, playerHeads);
    }
}