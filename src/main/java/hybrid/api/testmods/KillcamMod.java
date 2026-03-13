package hybrid.api.testmods;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.NumberSetting;

import java.util.List;

public class KillcamMod extends HybridMod {

    public KillcamMod() {
        super("kill-cam", 0.5f);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("Killcam Settings")
                        .add(new NumberSetting("Killcam Duration",5,1,15).onChange(v->System.out.println("Killcam duration "+v)))
                        .add(new NumberSetting("Replay Speed",1,1,5).onChange(v->System.out.println("Replay speed "+v)))
                        .add(new BooleanSetting("Slow Motion",true).onChange(v->System.out.println("Slow motion "+v)))
                        .add(new BooleanSetting("Show Killer Name",true).onChange(v->System.out.println("Show killer "+v)))
                        .add(new BooleanSetting("Cinematic Camera",true).onChange(v->System.out.println("Cinematic camera "+v)))
                        .add(new ModeSetting<>("Camera Mode",KillcamMode.THIRD_PERSON))
                        .add(new ModeSetting<>("Transition Animation",TransitionMode.FADE))
                        .build()
        );
    }

    public enum KillcamMode {
        FIRST_PERSON,
        THIRD_PERSON,
        FREE_CAM,
        FOLLOW_KILLER
    }

    public enum TransitionMode {
        FADE,
        ZOOM,
        SPIN,
        CUT
    }
}