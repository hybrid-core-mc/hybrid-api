package hybrid.api.mods.settings;

import com.google.gson.JsonObject;

public class NumberSetting extends ModSetting<Float> {

    private final float min;
    private final float max;

    public NumberSetting(String name, float defaultValue, float min, float max) {
        super(name, clamp(defaultValue, min, max));
        this.min = min;
        this.max = max;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

    @Override
    public void set(Float newValue) {
        super.set(clamp(newValue, min, max));
    }

    @Override
    public void readJson(JsonObject json) {
        if (json == null) return;
        if (!json.has(name)) return;

        try {
            set(json.get(name).getAsNumber().floatValue());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void writeJson(JsonObject json) {
        json.addProperty(name, get());
    }
}