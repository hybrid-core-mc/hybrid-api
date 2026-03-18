package hybrid.api.theme;

import hybrid.api.HybridApi;
import hybrid.api.mod.HybridMod;
import hybrid.api.mod.ModLink;
import hybrid.api.mod.category.ModCategorySettingBuilder;
import hybrid.api.mod.category.ModSettingCategory;
import hybrid.api.mod.settings.BooleanSetting;

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

    @Override
    protected ModLink getGithub() {
        return null;
    }

    @Override
    protected ModLink getModrinth() {
        return null;
    }
}
