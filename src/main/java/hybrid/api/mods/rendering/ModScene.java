package hybrid.api.mods.rendering;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.HybridMods;

public abstract class ModScene {
    HybridMod mod;

    public ModScene(HybridMod mod) {
        this.mod = mod;
    }
}
