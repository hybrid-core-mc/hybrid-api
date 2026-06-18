package hybrid.api.mod;

import java.util.ArrayList;
import java.util.List;

public class HybridMods {

    private static final List<HybridMod> MODS = new ArrayList<>();

    public static void register(HybridMod mod) {
        MODS.add(mod);
    }

    public static void init() {

        register(new SprintMod());

        for (HybridMod mod : MODS) {
            mod.onInitialize();
            mod.onSetupSettings();
        }
    }



    public static List<HybridMod> getMods() {
        return MODS;
    }

    public static <T extends HybridMod> T getMod(Class<T> clazz) {
        for (HybridMod mod : MODS) {
            if (clazz.isInstance(mod)) {
                return clazz.cast(mod);
            }
        }
        return null;
    }
}