package hybrid.api.mod;

import hybrid.api.HybridApi;
import hybrid.api.mod.category.ModCategory;
import hybrid.api.testmods.chatplus.ChatPlusMod;
import hybrid.api.theme.SystemSettingsMod;
import hybrid.api.theme.SystemThemeMod;

import java.util.ArrayList;
import java.util.List;

public class HybridMods {
    public static List<HybridMod> mods = new ArrayList<>();
    public static List<HybridMod> systemMods = new ArrayList<>();

    static {

        mods.add(new ChatPlusMod());


        mods.forEach(HybridMod::init);

        systemMods.add(new SystemThemeMod());
        systemMods.add(new SystemSettingsMod());
        systemMods.forEach(HybridMod::init);
    }

    public static void init() {
        for (HybridMod mod : mods) {

            mod.init();

            mod.onInitialize();

            HybridApi.EVENT_BUS.register(mod);

            for (ModCategory category : mod.categories) {
                HybridApi.EVENT_BUS.register(category);
            }
        }
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
