package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.theme.HybridTheme;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;

import java.awt.*;
import java.util.List;

public class ThemeMod extends HybridMod {


    private static final ModeSetting<ThemeMode> themeMode = new ModeSetting<>("Theme Mode", ThemeMode.Auto);
    private static final ModeSetting<ThemeColor> colorOption = new ModeSetting<>("Color Option", ThemeColor.Background);


    public ThemeMod() {
        super("theme-mod", "Change the colors and shit\n FOR MOANIGN BEAR XDDD LOL", 0.f);
    }

    private static void updateColor(Color c) {
        if (themeMode.get() == ThemeMode.Manual) {
            switch (colorOption.get()) {

            }
        } else {

        }

    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("General")
                        .add(themeMode)
                        .add(colorOption)
                        .add(new ColorSetting("The color", Color.PINK).onChange(ThemeMod::updateColor))
                        .add(new NumberSetting("Corner Radius", 8, 1, 30).onChange(v -> {
                            HybridTheme.cornerRadius = v.intValue();
                        }))
                        .build(),
                new ModCategorySettingBuilder("Saved")
                        .add(themeMode)
                        .add(colorOption)
                        .add(new ColorSetting("The color", Color.PINK).onChange(ThemeMod::updateColor))
                        .add(new NumberSetting("Corner Radius", 8, 1, 30).onChange(v -> {
                            HybridTheme.cornerRadius = v.intValue();
                        }))
                        .build()
        );
    }

    public enum ThemeMode {
        Auto, Manual
    }

    public enum ThemeColor {
        Background, ModsBackground, ModBackground, Border, Border1
    }

}
