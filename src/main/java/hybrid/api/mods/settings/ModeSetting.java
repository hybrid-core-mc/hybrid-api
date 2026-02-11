package hybrid.api.mods.settings;

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
}