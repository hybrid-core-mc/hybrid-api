package hybrid.api.mod.chat.parts.msgs;

import hybrid.api.mod.chat.parts.AvatarRenderer;
import hybrid.api.mod.chat.parts.ChatTextComponent;
import hybrid.api.mod.chat.parts.ChatLayoutController;
import hybrid.api.util.render.Quad;
import hybrid.api.util.texture.HybridTextureRenderer;

public abstract class RenderableMessage {
    protected final ChatTextComponent.ChatMessage msg;
    protected final boolean isGrouped;
    protected final Quad clipping;
    protected final boolean playerHeads;

    public RenderableMessage(ChatTextComponent.ChatMessage msg, boolean isGrouped, Quad clipping, boolean playerHeads) {
        this.msg = msg;
        this.isGrouped = isGrouped;
        this.clipping = clipping;
        this.playerHeads = playerHeads;
    }

    
    public abstract float getHeight();

    public void render(HybridTextureRenderer textureRenderer, float startX, float startY, Quad baseClipping, boolean playerHeads, float anchorHeight) {
        float contentX = ChatLayoutController.getContentX(startX, playerHeads);
        Quad textClipping = baseClipping.copy().subtractHeight((int) (anchorHeight + 8));
        float bodyY;

        if (!isGrouped) {
            
            if (playerHeads && msg.skin() != null) {
                AvatarRenderer.render(textureRenderer, startX, startY, msg.skin(), clipping);
            }

            
            UsernameRenderer.render(msg.senderName(), contentX, startY, textClipping);

            
            bodyY = startY + (ChatLayoutController.getUsernameFontSize() + ChatLayoutController.usernameToTextSpacing);
        } else {
            bodyY = startY;
        }

        
        renderContent(textureRenderer, contentX, bodyY, textClipping);
    }

    protected abstract void renderContent(HybridTextureRenderer textureRenderer, float contentX, float bodyY, Quad textClipping);
}