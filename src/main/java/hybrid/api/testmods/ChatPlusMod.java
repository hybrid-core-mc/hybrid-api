package hybrid.api.testmods;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.mods.settings.TextListSetting;

import java.util.List;

public class ChatPlusMod extends HybridMod {
    public ChatPlusMod() {
        super("chat-plus", "Adds emojis to the minecraft chat its cool.\n TBh moaning bear is so maoning LOL xd", 0.f);
    }


    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("Chat")
                        .add(new TextListSetting("Filter", List.of("Testing","Bear")))
                        .add(new BooleanSetting("Automatically Replace", true).onChange(v -> System.out.println("changed to " + v)))
                        .add(new NumberSetting("Max Emojis", 5, 1, 100).onChange(v -> System.out.println("updated new value to " + v)))
                        .add(new NumberSetting("magic", 5, 1, 100).onChange(v -> System.out.println("updated new value to " + v)))
                        .add(new ModeSetting<>("Mode", TestMode.BOMBACLAT))
                        .add(new BooleanSetting("testing bear", true).onChange(v -> System.out.println("changed to " + v)))
                        .build()

        );
    }

    enum TestMode {
        FUNNYSS, BOMBACLAT, TESTINGG
    }

}
