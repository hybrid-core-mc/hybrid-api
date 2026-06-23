package hybrid.api.util.chat.parts;

import hybrid.api.Main;
import hybrid.api.util.render.ExternalImageRenderer;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import hybrid.api.util.texture.HybridTexture;
import hybrid.api.util.texture.HybridTextureRenderer;
import hybrid.api.util.texture.PlayerInfoAccessor;
import hybrid.api.util.texture.TextureCache;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.special.PlayerHeadSpecialRenderer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static hybrid.api.Main.mc;

public class ChatTextComponent {

    public enum ChatType {
        TEXT,
        GIF
    }

    public record ChatMessage(String senderName, int index, String message, ChatType type, String gifPath) {}


    private final HybridTextureRenderer hybridTextureRenderer = new HybridTextureRenderer();

    private final List<ChatMessage> messageHistory = new ArrayList<>();
    private int messageIndexCounter = 0;

    public ChatTextComponent() {
    }

    public void render(Quad anchorQuad, Quad clipping) {
        if (messageHistory.isEmpty()) return;

        float currentY = anchorQuad.getY() - 5;
        int renderedCount = 0;

        List<PlayerInfo> playerInfos = ((PlayerInfoAccessor) mc.gui.getTabList()).hybrid_api$playerInfo();

        for (int i = messageHistory.size() - 1; i >= 0; i--) {
            if (renderedCount >= LayoutController.MAX_VISIBLE_MESSAGES) break;

            ChatMessage msg = messageHistory.get(i);
            boolean isGrouped = (i > 0) && messageHistory.get(i - 1).senderName().equals(msg.senderName());

            RenderableMessage renderBlock = new RenderableMessage(msg, isGrouped, clipping);


            currentY -= LayoutController.getLayoutMargin(isGrouped);


            currentY -= renderBlock.getHeight();


            renderBlock.render(anchorQuad.getX(), currentY, anchorQuad.getWidth(), clipping, anchorQuad.height, playerInfos);

            renderedCount++;
        }
    }

    public List<ChatMessage> getMessageHistory() {
        return messageHistory;
    }

    public void addMessage(String senderName, String message) {
        messageHistory.add(new ChatMessage(senderName, messageIndexCounter++, message, ChatType.TEXT, null));
    }

    public void submitGif(String senderName, String path) {
        messageHistory.add(new ChatMessage(senderName, messageIndexCounter++, "[GIF]", ChatType.GIF, path));
    }

    public static class LayoutController {

        public static final int MAX_VISIBLE_MESSAGES = 10;
        public static float chatFontSize = 11f;
        public static float usernameFontSize = 10f;
        public static int headSize = 20;
        public static int headPaddingX = 6;
        public static int textPaddingAfterHead = 6;
        public static float avatarBottomPadding = 2f;
        public static float usernameToTextSpacing = 3f;
        public static int gifWidth = 40;
        public static int gifHeight = 40;

        public static float getAvatarX(float startX) {
            return startX + headPaddingX;
        }

        public static float getContentX(float startX) {
            return startX + headPaddingX + headSize + textPaddingAfterHead;
        }


        public static float calculateVisualHeight(ChatType type, boolean isGrouped) {
            float contentHeight = (type == ChatType.GIF) ? gifHeight : chatFontSize;
            if (!isGrouped) {

                float textHeight = usernameFontSize + usernameToTextSpacing + contentHeight;

                float minHeaderHeight = headSize + avatarBottomPadding;

                return Math.max(textHeight, minHeaderHeight);
            } else {
                return contentHeight;
            }
        }


        public static float getLayoutMargin(boolean isGrouped) {
            return 4;
        }
    }

    private class RenderableMessage {
        private final ChatMessage msg;
        private final boolean isGrouped;
        private final float visualHeight;
        Quad clipping;

        public RenderableMessage(ChatMessage msg, boolean isGrouped, Quad clipping) {
            this.msg = msg;
            this.isGrouped = isGrouped;
            this.clipping = clipping;
            this.visualHeight = LayoutController.calculateVisualHeight(msg.type(), isGrouped);
        }

        public float getHeight() {
            return this.visualHeight;
        }

        public void render(float startX, float startY, float blockWidth, Quad baseClipping, float anchorHeight, List<PlayerInfo> playerInfos) {
            float contentX = LayoutController.getContentX(startX);
            Quad textClipping = baseClipping.copy().subtractHeight((int) (anchorHeight + 8));
            float bodyY;

            PlayerInfo info = null;
            for (PlayerInfo playerInfo : playerInfos) {
                if(playerInfo.getProfile().equals(mc.player.getGameProfile())) info = playerInfo;
            }


            if (!isGrouped) {

                if(info != null) {
                    renderAvatar(startX, startY, info);
                }
                Main.RENDERER.drawText(
                        Main.getStyle(), msg.senderName(), contentX, startY,
                        LayoutController.usernameFontSize, 0xFFFFFFFF, textClipping
                );

                bodyY = startY + (LayoutController.usernameFontSize + LayoutController.usernameToTextSpacing);
            } else {
                bodyY = startY;
            }

            renderContent(contentX, bodyY, textClipping);
        }

        private void renderAvatar(float startX, float startY, PlayerInfo playerInfo) {

            if (playerInfo != null) {
                playerInfo.getSkin();
                playerInfo.getSkin();
                HybridTexture skinTexture = TextureCache.getOrCreate(playerInfo.getSkin().body().texturePath());

                float avatarX = LayoutController.getAvatarX(startX);
                hybridTextureRenderer.drawTextureSubRegion(
                        skinTexture, avatarX, startY, LayoutController.headSize, LayoutController.headSize,
                        8, 8, 8, 8, 0xFFFFFFFF, 1f, false
                );
                hybridTextureRenderer.setClip(clipping);
                hybridTextureRenderer.flush();
            }
        }

        private void renderContent(float contentX, float bodyY, Quad textClipping) {
            if (msg.type() == ChatType.TEXT) {
                Main.RENDERER.drawText(
                        Main.getStyle(), msg.message(), contentX, bodyY,
                        LayoutController.chatFontSize, 0xFFD0D0D0, textClipping
                );
            } else if (msg.type() == ChatType.GIF) {
                Path path = Paths.get(msg.gifPath());
                ExternalImageRenderer.renderGif(hybridTextureRenderer,
                        RenderContext.get(), path, contentX, bodyY,
                        LayoutController.gifWidth, LayoutController.gifHeight
                );
                hybridTextureRenderer.setClip(clipping);
            }
        }
    }
}