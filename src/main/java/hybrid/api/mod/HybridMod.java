package hybrid.api.mod;

import hybrid.api.mod.settings.BuiltCategory;

import java.util.ArrayList;
import java.util.List;

public abstract class HybridMod {

    protected String name,description;
    protected float version;

    private final List<BuiltCategory> categories = new ArrayList<>();

    public HybridMod(String name, String description, float version) {
        this.name = name;
        this.description = description;
        this.version = version;
    }

    public abstract void onInitialize();

    public abstract void onSetupSettings();

    public void registerCategory(BuiltCategory category) {
        categories.add(category);
    }

    public String getDescription() {
        return description;
    }

    public List<BuiltCategory> getCategories() {
        return categories;
    }

    public String getName() {
        return name;
    }

    public float getVersion() {
        return version;
    }
}