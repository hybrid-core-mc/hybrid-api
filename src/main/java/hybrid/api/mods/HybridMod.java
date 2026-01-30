package hybrid.api.mods;

import hybrid.api.mods.category.ModSettingCategory;

import java.util.ArrayList;
import java.util.List;

public abstract class HybridMod {
    String name;
    float version;
    protected final List<ModSettingCategory> modSettingCategories = new ArrayList<>();

    public HybridMod(String name, float version) {
        this.name = name;
        this.version = version;
        modSettingCategories.addAll(createSettings());
    }

    protected abstract List<ModSettingCategory> createSettings();

    public String getName() {
        return name;
    }

    public String getDesc(){
        return "Adds emojis to the minecraft chat its cool.\n TBh moaning bear is so maoning LOL xd";
    }
    public List<ModSettingCategory> getCategories() {
        return modSettingCategories;
    }
}
