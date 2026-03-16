package hybrid.api.theme;

import hybrid.api.HybridApi;
import hybrid.api.mods.HybridMod;
import hybrid.api.mods.category.ModCategorySettingBuilder;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.BooleanSetting;

import java.util.List;

public class SystemSettingsMod extends HybridMod {
    public boolean vanillaFonts;

    public SystemSettingsMod() {
        super("settings", HybridApi.VERSION);
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                new ModCategorySettingBuilder("Font")
                        .add(new BooleanSetting("Vanilla", false).onChange(
                                aBoolean -> vanillaFonts = aBoolean
                        ))
                        .build()
        );
    }
}
