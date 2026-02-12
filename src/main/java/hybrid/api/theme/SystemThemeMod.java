package hybrid.api.theme;

import hybrid.api.HybridApi;
import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.mods.settings.ModeSetting;

import java.awt.*;
import java.util.List;

import static hybrid.api.HybridApi.mc;

public  class SystemThemeMod extends HybridMod {

    public ModeSetting<ThemeColorKey> colorTarget =
            new ModeSetting<>("Color Target", ThemeColorKey.backgroundColor);

    public SystemThemeMod() {
        super("Themes", "Customize the UI\nChange existing colours", HybridApi.VERSION);
        setSaveSettings(false);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("Colors")
                        .add(colorTarget)
                        .add(new ColorSetting("Mono color", Color.PINK).onChange(this::applyColor))
                        .build()
        );
    }

    private void applyColor(Color color) {
        if (mc.world == null) return;
        HybridThemeMap.set(colorTarget.get(), color);
    }

}