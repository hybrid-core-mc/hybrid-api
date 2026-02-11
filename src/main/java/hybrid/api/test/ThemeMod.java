package hybrid.api.test;

import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.mods.settings.ModeSetting;
import hybrid.api.mods.settings.NumberSetting;
import hybrid.api.theme.Theme;

import java.awt.*;
import java.util.List;

public class ThemeMod extends HybridMod {


    private static final ModeSetting<ThemeMode> themeMode = new ModeSetting<>("Theme Mode", ThemeMode.Auto);
    private static final ModeSetting<ThemeColor> colorOption = new ModeSetting<>("Color Option", ThemeColor.Background);


    public ThemeMod() {
        super("Theme mod", "Change the colors and shit\n FOR MOANIGN BEAR XDDD LOL", 0.f);
    }

    private static void updateColor(Color c) {
        if (themeMode.get() == ThemeMode.Manual) {
            switch (colorOption.get()) {
                case Background -> Theme.backgroundColor = c;
                case ModsBackground -> Theme.modsBackgroundColor = c;
                case ModBackground -> Theme.modBackgroundColor = c;
            }
        } else {
            Theme.backgroundColor = c.darker();
            Theme.modBackgroundColor = c.brighter();
            Theme.modsBackgroundColor = c;
        }

    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("General")
                        .add(themeMode)
                        .add(colorOption)
                        .add(new ColorSetting("The color", Color.PINK).onChange(ThemeMod::updateColor))
                        .add(new NumberSetting("Chaos Level", 50, 0, 100).onChange(v -> System.out.println("Chaos Level = " + v)))
                        .build()
        );
    }

    public enum ThemeMode {
        Auto, Manual
    }

    public enum ThemeColor {
        Background, ModsBackground, ModBackground
    }

}
