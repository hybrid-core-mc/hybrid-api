package hybrid.api.mods;

import hybrid.api.mods.category.ModSettingCategory;

import java.util.ArrayList;
import java.util.List;

public abstract class HybridMod {
    String name,desc;
    float version;
    protected final List<ModSettingCategory> modSettingCategories = new ArrayList<>();
    public HybridMod(String name, float version) {
        this.name = name;
        this.version = version;
        modSettingCategories.addAll(createSettings());
    }

    public List<ModSettingCategory> getModSettingCategories() {
        return modSettingCategories;
    }

    public HybridMod(String name, String desc, float version) {
        this.name = name;
        this.desc = desc;
        this.version = version;
        modSettingCategories.addAll(createSettings());
    }

    protected abstract List<ModSettingCategory> createSettings();

    public String getName() {
        return name;
    }

    public String getDesc(){
        return desc;
    }
    public List<ModSettingCategory> getCategories() {
        return modSettingCategories;
    }
}
