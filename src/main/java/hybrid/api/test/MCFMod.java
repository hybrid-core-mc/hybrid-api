package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.ModSetting;

import java.util.List;

public class MCFMod extends HybridMod {
    public MCFMod() {
        super("MCF",1.0f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of();
    }
}
