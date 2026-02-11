package hybrid.api.mods;

import hybrid.api.test.*;

import java.util.ArrayList;
import java.util.List;

public class HybridMods {
    public static List<HybridMod> mods = new ArrayList<>();

    static {
        mods.add(new ThemeMod());
        mods.add(new DeathPlus());
        mods.add(new KillcamMod());
        mods.add(new MCFMod());
    }
}
