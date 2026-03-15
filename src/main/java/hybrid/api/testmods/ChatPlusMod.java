package hybrid.api.testmods;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.TextListSetting;

import java.util.List;

public class ChatPlusMod extends HybridMod {
    public ChatPlusMod() {
        super("chat-plus", "Adds emojis to the minecraft chat its cool.\n TBh moaning bear is so maoning LOL xd", 0.f);
    }


    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("Filter")
                        .add(new TextListSetting("Words", List.of("Cuss", "World")))
//                        .add(new ModeSetting<>("Mode", TestMode.Censor))
                        .build(),
                new ModCategorySettingBuilder("Highlight")
                        .add(new TextListSetting("Words", List.of("Cuss", "World")))
                        .add(new ModeSetting<>("Mode", TestMode.Censor))
                        .build()
        );
    }

    enum TestMode {
        Remove, Censor, Ignore
    }

}
