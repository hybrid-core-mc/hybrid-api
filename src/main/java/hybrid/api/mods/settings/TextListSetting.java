package hybrid.api.mods.settings;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class TextListSetting extends ModSetting<List<String>> {

    public TextListSetting(String name, List<String> defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public void writeJson(JsonObject json) {
        JsonArray array = new JsonArray();

        for (String text : get()) {
            array.add(text);
        }

        json.add(getName(), array);
    }

    @Override
    public void readJson(JsonObject json) {
        if (!json.has(getName())) return;

        JsonArray array = json.getAsJsonArray(getName());
        List<String> list = new ArrayList<>();

        for (JsonElement element : array) {
            list.add(element.getAsString());
        }

        set(list);
    }
}