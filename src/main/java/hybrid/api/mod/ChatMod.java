package hybrid.api.mod;

import hybrid.api.mod.settings.*;

public class ChatMod extends HybridMod {


    public NumberSetting maxMessages = new NumberSetting("Max Messages", "Maximum amount of messages visible in the chat history", 10, 1, 50);
    public NumberSetting chatFontSize = new NumberSetting("Chat Font Size", "Font size for the main chat message text", 11.0f, 6.0f, 24.0f);
    public NumberSetting usernameFontSize = new NumberSetting("Username Font Size", "Font size for the player names above messages", 11.0f, 6.0f, 20.0f);

    public BooleanSetting showPlayerHeads = new BooleanSetting("Show Player Heads", "Render player skin avatars next to usernames", true);
    public NumberSetting avatarSize = new NumberSetting("Avatar Size", "Width and height of the player head avatar", 20, 8, 40);
    public NumberSetting gifScale = new NumberSetting("GIF Scale", "Width of rendered animated GIF messages", 40, 10, 150);

    public ChatMod() {
        super("Chat Plus", "Chat Plus enhances chat with custom fonts, avatars, usernames, and inline media like animated GIFs", 1.0f);
    }

    @Override
    public void onInitialize() {
    }

    @Override
    public void onSetupSettings() {
        registerCategory(new BuiltCategory("Chat Layout")
                .add(maxMessages)
                .add(chatFontSize)
                .add(usernameFontSize)
        );

        registerCategory(new BuiltCategory("Avatars & Media")
                .add(showPlayerHeads)
                .add(avatarSize)
                .add(gifScale)
        );
    }

}