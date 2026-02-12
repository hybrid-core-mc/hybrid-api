package hybrid.api.mods.settings;

import com.google.gson.JsonObject;

public class ModeSetting<E extends Enum<E>> extends ModSetting<E> {

    private final E[] values;

    public ModeSetting(String name, E defaultValue) {
        super(name, defaultValue);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
    }


    public String getLongestValue() {
        String longest = values[0].name();

        for (E value : values) {
            String name = value.name();
            if (name.length() > longest.length()) {
                longest = name;
            }
        }

        return longest;
    }
    public void cycle() {
        doCycle(1);
    }

    public void cycleBack() {
        doCycle(-1);
    }

    private void doCycle(int direction) {
        E current = get();
        int index = current.ordinal();

        int rawNext = index + direction;
        int wrappedNext = (rawNext + values.length) % values.length;
        set(values[wrappedNext]);
    }

    @Override
    public void writeJson(JsonObject json) {
        json.addProperty(name, get().name());
    }

    @Override
    public void readJson(JsonObject json) {
        if (!json.has(name)) return;

        String valueName = json.get(name).getAsString();
        for (E value : values) {
            if (value.name().equals(valueName)) {
                set(value);
                return;
            }
        }
    }
}