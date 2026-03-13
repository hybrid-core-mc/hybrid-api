package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.NumberSetting;

import java.util.List;

public class MCFMod extends HybridMod {

    public MCFMod() {
        super("MCF",1.0f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("MCF Settings")
                        .add(new ModeSetting<>("Mode",MCFMode.FIX))
                        .add(new NumberSetting("Delay",100,0,500).onChange(v->System.out.println("Delay set to "+v)))
                        .build()
        );
    }

    public enum MCFMode {
        DOUBLE_CLICK,
        DELAY,
        FIX
    }
}