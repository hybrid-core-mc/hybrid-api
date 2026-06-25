package hybrid.api.mod.chat.parts.msgs;

import hybrid.api.Main;
import hybrid.api.mod.chat.parts.ChatTextComponent;
import hybrid.api.mod.chat.parts.ChatLayoutController;
import hybrid.api.util.render.Quad;
import hybrid.api.util.texture.HybridTextureRenderer;

public class TextRenderableMessage extends RenderableMessage {

    public TextRenderableMessage(ChatTextComponent.ChatMessage msg, boolean isGrouped, Quad clipping, boolean playerHeads) {
        super(msg, isGrouped, clipping, playerHeads);
    }

    @Override
    protected void renderContent(HybridTextureRenderer textureRenderer, float contentX, float bodyY, Quad textClipping) {
        Main.RENDERER.drawText(
                Main.getStyle(),
                msg.message(),
                contentX,
                bodyY,
                ChatLayoutController.getChatFontSize(),
                0xFFD0D0D0,
                textClipping
        );
    }
}