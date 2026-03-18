package hybrid.api.mod;

import com.google.gson.JsonObject;
import hybrid.api.mod.category.ModCategory;
import hybrid.api.mod.category.ModSettingCategory;
import hybrid.api.mod.settings.ModSetting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class HybridMod {

    String name, desc;
    float version;
    File folder;
    boolean saveSettings;

    protected final List<ModSettingCategory> modSettingCategories = new ArrayList<>();
    protected final List<ModCategory> categories = new ArrayList<>();

    protected ModSettingCategory registerCategory(ModCategory category) {
        categories.add(category);
        return category.toCategory();
    }
    public List<ModSettingCategory> getModSettingCategories() {
        return modSettingCategories;
    }

    public HybridMod(String name, float version) {
        this.name = name;
        this.version = version;
    }

    public HybridMod(String name, String desc, float version) {
        this.name = name;
        this.desc = desc;
        this.version = version;
    }

    public final void init() {
        modSettingCategories.clear();

        List<ModSettingCategory> settings = createSettings();
        if (settings != null) {
            modSettingCategories.addAll(settings);
        }
    }


    protected abstract List<ModSettingCategory> createSettings();

    protected abstract ModLink getGithub();

    protected abstract ModLink getModrinth();

    public void onInitialize() {

    }

    public JsonObject getJson() {
        JsonObject modJson = new JsonObject();
        for (ModSettingCategory modSettingCategory : modSettingCategories) {
            modJson.add(modSettingCategory.name(), modSettingCategory.getJson());
        }
        return modJson;
    }


    public void loadJson(JsonObject json) {
        if (json == null) return;

        for (ModSettingCategory category : modSettingCategories) {
            if (!json.has(category.name())) continue;

            JsonObject categoryJson = json.getAsJsonObject(category.name());

            for (ModSetting<?> setting : category.settings()) {
                setting.readJson(categoryJson);
            }
        }
    }


    public String getName() {
        return name;
    }

    public String getFormattedName() {
        String[] parts = name.split("-");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            sb.append(Character.toUpperCase(part.charAt(0)))
              .append(part.substring(1).toLowerCase())
              .append(" ");
        }

        return sb.toString().trim();
    }

    public String getDesc() {
        return desc == null ? "No description yet" : desc;
    }

    public File getFolder() {
        return folder;
    }

    public void setFolder(File folder) {
        this.folder = folder;
    }

    public void setSaveSettings(boolean saveSettings) {
        this.saveSettings = saveSettings;
    }


}