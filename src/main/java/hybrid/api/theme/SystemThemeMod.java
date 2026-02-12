package hybrid.api.theme;

import hybrid.api.HybridApi;
import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.BooleanSetting;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.NumberSetting;

import java.awt.*;
import java.util.List;

import static hybrid.api.HybridApi.mc;

public  class SystemThemeMod extends HybridMod {

    public ModeSetting<ThemeColorKey> colorTarget = new ModeSetting<>("Color Target", ThemeColorKey.backgroundColor);
    public ModeSetting<ColorMode> colorMode = new ModeSetting<>("Color Mode", ColorMode.Auto);

    public SystemThemeMod() {
        super("Themes", "Customize the UI\nChange existing colours", HybridApi.VERSION);
        setSaveSettings(false);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("Colors")
                        .add(colorMode)
                        .add(colorTarget.visible(() ->
                                colorMode.get() == ColorMode.Manual
                        ))
                        .add(new ColorSetting("Mono color", Color.PINK)
                                .onChange(this::applyColor))
                        .build(),
                new ModCategorySettingBuilder("Values")
                        .add(new BooleanSetting("Enable Chaos Mode", false).onChange(v -> System.out.println("Chaos Mode = " + v)))
                        .add(new NumberSetting("Chaos Level", 50, 0, 100).onChange(v -> System.out.println("Chaos Level = " + v)))
                        .add(new NumberSetting("Scroll Stress Test", 25, 1, 500).onChange(v -> System.out.println("Scroll Stress = " + v)))
                        .add(new BooleanSetting("Debug Overlay", true).onChange(v -> System.out.println("Debug Overlay = " + v)))
                        .add(new NumberSetting("FPS Limit", 144, 30, 360).onChange(v -> System.out.println("FPS Limit = " + v)))
                        .build()
        );
    }

    private void applyColor(Color color) {
        if (mc.world == null) return;
        HybridThemeMap.set(colorTarget.get(), color);
    }

    public enum ColorMode {
        Auto,
        Manual
    }

}