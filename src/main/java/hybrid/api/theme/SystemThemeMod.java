package hybrid.api.theme;

import hybrid.api.HybridApi;
import hybrid.api.mod.HybridMod;
import hybrid.api.mod.ModLink;
import hybrid.api.mod.category.ModCategorySettingBuilder;
import hybrid.api.mod.category.ModSettingCategory;
import hybrid.api.mod.settings.ColorSetting;
import hybrid.api.mod.settings.ModeSetting;

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
                        .add(new ColorSetting("Color", Color.PINK)
                                .onChange(this::applyColor))
                        .add(new ColorSetting("xd",Color.WHITE))
                        .build());
    }

    @Override
    protected ModLink getGithub() {
        return null;
    }

    @Override
    protected ModLink getModrinth() {
        return null;
    }

    private void applyColor(Color color) {
        if (mc.world == null) return;

        if (colorMode.get() == ColorMode.Auto) {
            HybridThemeMap.set(ThemeColorKey.modBackgroundColor, color);
            HybridThemeMap.set(ThemeColorKey.modsBackgroundColor, color.brighter());
            HybridThemeMap.set(ThemeColorKey.backgroundColor, color.darker());
        }
        HybridThemeMap.set(colorTarget.get(), color);
    }

    public enum ColorMode {
        Auto,
        Manual
    }

}