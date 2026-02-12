package hybrid.api.mods.settings;

import com.google.gson.JsonObject;

import java.awt.Color;

public class ColorSetting extends ModSetting<Color> {

    public ColorSetting(String name, Color defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public void writeJson(JsonObject json) {
        Color c = get();

        JsonObject col = new JsonObject();
        col.addProperty("r", c.getRed());
        col.addProperty("g", c.getGreen());
        col.addProperty("b", c.getBlue());
        col.addProperty("a", c.getAlpha());

        json.add(name, col);
    }

    @Override
    public void readJson(JsonObject json) {
        if (!json.has(name)) return;

        JsonObject col = json.getAsJsonObject(name);

        int r = col.has("r") ? col.get("r").getAsInt() : value.getRed();
        int g = col.has("g") ? col.get("g").getAsInt() : value.getGreen();
        int b = col.has("b") ? col.get("b").getAsInt() : value.getBlue();
        int a = col.has("a") ? col.get("a").getAsInt() : value.getAlpha();

        set(new Color(r, g, b, a));
    }
}