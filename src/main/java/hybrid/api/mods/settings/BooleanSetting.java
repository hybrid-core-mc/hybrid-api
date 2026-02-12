package hybrid.api.mods.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BooleanSetting extends ModSetting<Boolean> {

    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        set(!get());
    }

    @Override
    public void writeJson(JsonObject json) {
        json.addProperty(name, get());
    }

    @Override
    public void readJson(JsonObject json) {
        if (!json.has(name)) return;
        set(json.get(name).getAsBoolean());
    }
}
