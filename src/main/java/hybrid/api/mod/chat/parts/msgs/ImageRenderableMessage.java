package hybrid.api.mod.chat.parts.msgs;

import hybrid.api.mod.chat.parts.ChatTextComponent;
import hybrid.api.mod.chat.parts.ChatLayoutController;
import hybrid.api.util.render.ExternalImageRenderer;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import hybrid.api.util.texture.HybridTextureRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageRenderableMessage extends RenderableMessage {

    public ImageRenderableMessage(ChatTextComponent.ChatMessage msg, boolean isGrouped, Quad clipping, boolean playerHeads) {
        super(msg, isGrouped, clipping, playerHeads);
    }

    @Override
    public float getHeight() {
        return 40;
    }

    @Override
    protected void renderContent(HybridTextureRenderer textureRenderer, float contentX, float bodyY, Quad textClipping) {
        Path path = Paths.get(msg.gifPath());

        ExternalImageRenderer.renderGif(
                textureRenderer,
                RenderContext.get(),
                path,
                contentX,
                bodyY,
                ChatLayoutController.getGifWidth(),
                ChatLayoutController.getGifWidth()
        );

        textureRenderer.setClip(clipping);
    }
}