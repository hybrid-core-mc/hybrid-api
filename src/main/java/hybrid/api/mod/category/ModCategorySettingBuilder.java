package hybrid.api.mod.category;

import hybrid.api.mod.settings.ModSetting;

import java.util.ArrayList;
import java.util.List;

public class ModCategorySettingBuilder {

    private final String categoryName;
    private final List<ModSetting<?>> settings = new ArrayList<>();

    public ModCategorySettingBuilder(String categoryName) {
        this.categoryName = categoryName;
    }

    public ModCategorySettingBuilder add(ModSetting<?> setting) {
        settings.add(setting);
        return this;
    }

    public ModSettingCategory build() {
        return new ModSettingCategory(categoryName, List.copyOf(settings));
    }
}
