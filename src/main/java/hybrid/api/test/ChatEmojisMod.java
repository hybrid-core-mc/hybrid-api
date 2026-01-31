package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.mods.settings.NumberSetting;

import java.util.List;

public class ChatEmojisMod extends HybridMod {
    public ChatEmojisMod() {
        super("Chat Emoji", "Adds emojis to the minecraft chat its cool.\n TBh moaning bear is so maoning LOL xd", 0.f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("General")
                        .add(new BooleanSetting("Automatically Replace", true).onChange(aBoolean -> System.out.println("changed to" + aBoolean)))
                        .add(new NumberSetting("Max Emojis", 5, 1, 100).onChange(e -> System.out.println("updated new value to" + e)))
                        .build()
        );
    }
}
