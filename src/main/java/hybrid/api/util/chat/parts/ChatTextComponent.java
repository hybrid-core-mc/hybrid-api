package hybrid.api.util.chat.parts;

import hybrid.api.Main;
import hybrid.api.util.render.ExternalImageRenderer;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import hybrid.api.util.texture.HybridTexture;
import hybrid.api.util.texture.HybridTextureRenderer;
import hybrid.api.util.texture.TextureCache;
import net.minecraft.world.entity.player.PlayerSkin;

import java.nio.file.Path;
import java.nio.file.Paths;
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
            if (renderedCount >= LayoutController.MAX_VISIBLE_MESSAGES) break;

            ChatMessage msg = messageHistory.get(i);

            boolean isGrouped =
                    (i > 0) && messageHistory.get(i - 1).senderName().equals(msg.senderName());

            RenderableMessage block = new RenderableMessage(msg, isGrouped, clipping, playerHeads);

            currentY -= LayoutController.getLayoutMargin(isGrouped);
            currentY -= block.getHeight();

            block.render(anchorQuad.getX(), currentY, clipping, true, anchorQuad.height);

            renderedCount++;
        }
    }

    public void submitMessage(String name, String message, PlayerSkin skin) {
        messageHistory.add(new ChatMessage(
                name,
                messageIndexCounter++,
                message,
                ChatType.TEXT,
                null,
                skin
        ));
    }

    public List<ChatMessage> getMessageHistory() {
        return messageHistory;
    }

    public void submitGif(String senderName, String path) {
        messageHistory.add(new ChatMessage(
                senderName,
                messageIndexCounter++,
                "[GIF]",
                ChatType.GIF,
                path,
                null
        ));
    }

    public record ChatMessage(
            String senderName,
            int index,
            String message,
            ChatType type,
            String gifPath,
            PlayerSkin skin
    ) {
    }

    public static class LayoutController {

        public static final int MAX_VISIBLE_MESSAGES = 10;
        public static float chatFontSize = 11f;
        public static float usernameFontSize = 10f;

        public static int headSize = 20;
        public static int headPaddingX = 6;
        public static int textPaddingAfterHead = 6;

        public static float usernameToTextSpacing = 3f;

        public static int gifWidth = 40;
        public static int gifHeight = 40;

        public static float getContentX(float startX, boolean playerHeads) {
            if (!playerHeads) return startX + textPaddingAfterHead;
            return startX + headPaddingX + headSize + textPaddingAfterHead;
        }

        public static float getAvatarX(float startX) {
            return startX + headPaddingX;
        }

        public static float calculateVisualHeight(ChatType type, boolean isGrouped, boolean playerHeads) {
            float contentHeight = (type == ChatType.GIF) ? gifHeight : chatFontSize;

            if (isGrouped) return contentHeight;

            if (!playerHeads) {
                float textHeight = usernameFontSize + usernameToTextSpacing + contentHeight;
                return textHeight;
            }

            float textHeight = usernameFontSize + usernameToTextSpacing + contentHeight;
            float minHeaderHeight = headSize;

            return Math.max(textHeight, minHeaderHeight);
        }

        public static float getLayoutMargin(boolean isGrouped) {
            return 4;
        }
    }

    private class RenderableMessage {

        private final ChatMessage msg;
        private final boolean isGrouped;
        private final float visualHeight;
        private final Quad clipping;

        public RenderableMessage(ChatMessage msg, boolean isGrouped, Quad clipping, boolean playerHeads) {
            this.msg = msg;
            this.isGrouped = isGrouped;
            this.clipping = clipping;
            this.visualHeight = LayoutController.calculateVisualHeight(msg.type(), isGrouped, playerHeads);
        }

        public float getHeight() {
            return visualHeight;
        }

        public void render(float startX, float startY, Quad baseClipping, boolean playerHeads, float anchorHeight) {

            float contentX = LayoutController.getContentX(startX, playerHeads);
            Quad textClipping = baseClipping.copy().subtractHeight((int) (anchorHeight + 8));

            float bodyY;

            if (!isGrouped) {

                if (playerHeads && msg.skin() != null) {
                    renderAvatar(startX, startY, msg.skin());
                }

                Main.RENDERER.drawText(
                        Main.getStyle(),
                        msg.senderName(),
                        contentX,
                        startY,
                        LayoutController.usernameFontSize,
                        0xFFFFFFFF,
                        textClipping
                );

                bodyY = startY + (LayoutController.usernameFontSize + LayoutController.usernameToTextSpacing);
            } else {
                bodyY = startY;
            }

            renderContent(contentX, bodyY, textClipping);
        }

        private void renderAvatar(float startX, float startY, PlayerSkin skin) {

            HybridTexture skinTexture =
                    TextureCache.getOrCreate(skin.body().texturePath());

            float avatarX = LayoutController.getAvatarX(startX);

            hybridTextureRenderer.drawTextureSubRegion(
                    skinTexture,
                    avatarX,
                    startY,
                    LayoutController.headSize,
                    LayoutController.headSize,
                    8, 8, 8, 8,
                    0xFFFFFFFF,
                    1f,
                    false
            );

            hybridTextureRenderer.setClip(clipping);
            hybridTextureRenderer.flush();
        }

        private void renderContent(float contentX, float bodyY, Quad textClipping) {

            if (msg.type() == ChatType.TEXT) {
                Main.RENDERER.drawText(
                        Main.getStyle(),
                        msg.message(),
                        contentX,
                        bodyY,
                        LayoutController.chatFontSize,
                        0xFFD0D0D0,
                        textClipping
                );
            } else if (msg.type() == ChatType.GIF) {
                Path path = Paths.get(msg.gifPath());

                ExternalImageRenderer.renderGif(
                        hybridTextureRenderer,
                        RenderContext.get(),
                        path,
                        contentX,
                        bodyY,
                        LayoutController.gifWidth,
                        LayoutController.gifHeight
                );

                hybridTextureRenderer.setClip(clipping);
            }
        }
    }
}