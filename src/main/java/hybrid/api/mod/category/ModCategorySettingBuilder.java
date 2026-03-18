package hybrid.api.mod.category;

import hybrid.api.mod.settings.ModSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModCategorySettingBuilder {

    private final String categoryName;
    private final List<ModSetting<?>> categories = new ArrayList<>();

    public ModCategorySettingBuilder(String categoryName) {
        this.categoryName = categoryName;
    }

    public ModCategorySettingBuilder add(ModSetting<?>... settings) {
        categories.addAll(Arrays.asList(settings));
        return this;
    }

    public ModSettingCategory build() {
        return new ModSettingCategory(categoryName, List.copyOf(categories));
    }
}
