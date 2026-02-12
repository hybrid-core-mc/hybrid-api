package hybrid.api.mods;

import hybrid.api.test.DeathPlus;
import hybrid.api.test.KillcamMod;
import hybrid.api.test.MCFMod;
import hybrid.api.test.ThemeMod;
import hybrid.api.theme.SystemThemeMod;

import java.util.ArrayList;
import java.util.List;

public class HybridMods {
    public static List<HybridMod> mods = new ArrayList<>();
    public static List<HybridMod> systemMods = new ArrayList<>();

    static {
        mods.add(new ThemeMod());
        mods.add(new DeathPlus());
        mods.add(new KillcamMod());
        mods.add(new MCFMod());

        mods.forEach(HybridMod::init);

        systemMods.add(new SystemThemeMod());
        systemMods.forEach(HybridMod::init);

    }
}
