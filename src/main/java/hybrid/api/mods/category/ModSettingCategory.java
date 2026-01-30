package hybrid.api.mods.category;

import hybrid.api.mods.settings.ModSetting;

import java.util.List;

public record ModSettingCategory(String name, List<ModSetting<?>> settings) {
}
