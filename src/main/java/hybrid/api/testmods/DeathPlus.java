package hybrid.api.testmods;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.NumberSetting;

import java.util.List;

public class DeathPlus extends HybridMod {

    public DeathPlus() {
        super("death-plus", 1.f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(new ModCategorySettingBuilder("Death Settings")

                .add(new NumberSetting("Death Delay", 2, 0, 10).onChange(v -> System.out.println("Death delay set to " + v + " seconds")))
                .add(new NumberSetting("Animation Duration", 3, 1, 10).onChange(v -> System.out.println("Animation duration changed to " + v)))
                .add(new NumberSetting("Max Emojis", 5, 1, 100).onChange(v -> System.out.println("updated new value to " + v)))
                .add(new BooleanSetting("Enable Death Animation", true).onChange(v -> System.out.println("Death animation enabled: " + v)))
                .add(new BooleanSetting("Screen Fade Effect", true).onChange(v -> System.out.println("Fade effect: " + v)))
                .add(new BooleanSetting("Auto Respawn", false).onChange(v -> System.out.println("Auto respawn: " + v)))
                .add(new ModeSetting<>("Death Message Style", DeathMessageMode.CLASSIC))
                .add(new ModeSetting<>("Death Animation", DeathAnimation.FADE))

                .build());
    }

    public enum DeathAnimation {
        FADE, ZOOM, SHAKE, SPIN, EXPLODE
    }

    public enum DeathMessageMode {
        CLASSIC, FUNNY, DRAMATIC, EMOJI, MINIMAL
    }
}