package hybrid.api.mods;

import java.util.ArrayList;
import java.util.List;

public class HybridMods {
    public static List<HybridMod> mods = new ArrayList<>();

    static {
        mods.add(new HybridMod("Mono Bao", 1.67f));
        mods.add(new HybridMod("Kill Cam", 1));
        mods.add(new HybridMod("Test lol", 1));
    }
}
