package hybrid.api.theme;

import hybrid.api.HybridApi;
import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.ColorSetting;
import hybrid.api.mods.settings.ModeSetting;

import java.awt.*;
import java.util.List;

public class SystemThemeMod extends HybridMod {

    private final ModeSetting<ThemeColorKey> colorTarget =
            new ModeSetting<>("Color Target", ThemeColorKey.backgroundColor);

    public SystemThemeMod() {
        super("Themes", "Customize the UI\nChange existing colours", HybridApi.VERSION);
        setSaveSettings(false);
    }

    private void updateColor(Color c) {
        ThemeColorKey key = colorTarget.get();
        HybridThemeMap.set(key, c);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("Colors")
                        .add(colorTarget)
                        .add(new ColorSetting("Mono color", Color.PINK)
                                .onChange(this::updateColor))
                        .build()
        );
    }
}