package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModSettingCategory;

import java.util.List;

public class DeathPlus extends HybridMod {
    public DeathPlus() {
        super("death-plus", 1.f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of();
    }
}
