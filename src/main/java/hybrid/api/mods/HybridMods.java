package hybrid.api.mods;

import hybrid.api.testmods.*;
import hybrid.api.theme.SystemSettingsMod;
import hybrid.api.theme.SystemThemeMod;

import java.util.ArrayList;
import java.util.List;

public class HybridMods {
    public static List<HybridMod> mods = new ArrayList<>();
    public static List<HybridMod> systemMods = new ArrayList<>();

    static {

        mods.add(new ChatPlusMod());
        mods.add(new DeathPlus());
        mods.add(new KillcamMod());
        mods.add(new MCFMod());

        mods.forEach(HybridMod::init);

        systemMods.add(new SystemThemeMod());
        systemMods.add(new SystemSettingsMod());
        systemMods.forEach(HybridMod::init);
    }
    public static <T extends HybridMod> T getSystemMod(Class<T> clazz) {
        for (HybridMod mod : systemMods) {
            if (clazz.isInstance(mod)) {
                return clazz.cast(mod);
            }
        }
        return null;
    }

}
