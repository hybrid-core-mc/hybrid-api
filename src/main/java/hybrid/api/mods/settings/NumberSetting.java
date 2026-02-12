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
        System.out.println("[NumberSetting] ---- readJson START ----");
        System.out.println("[NumberSetting] Setting name = \"" + name + "\"");

        if (json == null) {
            System.err.println("[NumberSetting] JSON object is NULL");
            return;
        }

        System.out.println("[NumberSetting] JSON keys = " + json.keySet());

        if (!json.has(name)) {
            System.err.println("[NumberSetting] Key not found: \"" + name + "\"");
            return;
        }

        try {
            if (json.get(name) == null) {
                System.err.println("[NumberSetting] json.get(name) returned NULL");
                return;
            }

            if (!json.get(name).isJsonPrimitive()) {
                System.err.println("[NumberSetting] Value is NOT a primitive: " + json.get(name));
                return;
            }

            float value = json.get(name).getAsNumber().floatValue();

            System.out.println("[NumberSetting] Loaded value = " + value);
            System.out.println("[NumberSetting] Before set(): current = " + get());

            set(value);

            System.out.println("[NumberSetting] After set(): current = " + get());

        } catch (Exception e) {
            System.err.println("[NumberSetting] FAILED to load setting: \"" + name + "\"");
            System.err.println("[NumberSetting] Raw JSON value = " + json.get(name));
            e.printStackTrace();
        }

        System.out.println("[NumberSetting] ---- readJson END ----");
    }

    @Override
    public void writeJson(JsonObject json) {
        json.addProperty(name, get());
    }
}