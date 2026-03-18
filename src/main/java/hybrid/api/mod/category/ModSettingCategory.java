package hybrid.api.mod.category;

import com.google.gson.JsonObject;
import hybrid.api.mod.settings.ModSetting;

import java.util.List;


public record ModSettingCategory(String name, List<ModSetting<?>> settings) {

    public JsonObject getJson() {
        JsonObject categoryJson = new JsonObject();

        for (ModSetting<?> setting : settings) {
            setting.writeJson(categoryJson);
        }

        return categoryJson;
    }
}