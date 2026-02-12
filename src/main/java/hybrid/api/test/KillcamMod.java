package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModSettingCategory;

import java.util.List;

public class KillcamMod extends HybridMod {
    public KillcamMod() {
        super("kill-cam", 0.5f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of();
    }
}
