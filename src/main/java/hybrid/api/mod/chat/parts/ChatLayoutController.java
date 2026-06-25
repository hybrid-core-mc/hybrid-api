package hybrid.api.mod.chat.parts;

import hybrid.api.mod.ChatMod;
import hybrid.api.mod.HybridMods;

public class ChatLayoutController {

    
    public static int getMaxVisibleMessages() { return getChatMod().maxMessages.getInt(); }
    public static float getChatFontSize() { return getChatMod().chatFontSize.get(); }
    public static float getUsernameFontSize() { return getChatMod().usernameFontSize.get(); } 

    public static int getHeadSize() { return getChatMod().avatarSize.getInt(); }
    public static int headPaddingX = 6; 
    public static int textPaddingAfterHead = 6;
    public static boolean isChatHeads() { return getChatMod().showPlayerHeads.get(); }

    public static float usernameToTextSpacing = 3f;

    public static int getGifWidth() { return getChatMod().gifScale.getInt(); }
    public static int getGifHeight() { return getChatMod().gifScale.getInt(); }

    public static float getContentX(float startX, boolean playerHeads) {
        if (!playerHeads) return startX + textPaddingAfterHead;
        return startX + headPaddingX + getHeadSize() + textPaddingAfterHead;
    }

    public static float getAvatarX(float startX) {
        return startX + headPaddingX;
    }

    public static float calculateVisualHeight(ChatTextComponent.ChatType type, boolean isGrouped, boolean playerHeads) {
        
        float contentHeight = (type == ChatTextComponent.ChatType.GIF) ? getGifHeight() : getChatFontSize();

        if (isGrouped) return contentHeight;

        if (!playerHeads) {
            return getUsernameFontSize() + usernameToTextSpacing + contentHeight;
        }

        float textHeight = getUsernameFontSize() + usernameToTextSpacing + contentHeight;
        float minHeaderHeight = getHeadSize();

        return Math.max(textHeight, minHeaderHeight);
    }

    public static float getLayoutMargin(boolean isGrouped) {
        return 4;
    }

    private static ChatMod getChatMod() {
        return HybridMods.getMod(ChatMod.class);
    }
}