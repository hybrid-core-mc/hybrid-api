package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModSettingCategory;

import java.util.List;

public class ChatEmojisMod extends HybridMod {
    public ChatEmojisMod() {
        super("Chat Emoji",0.f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of();
    }
}
