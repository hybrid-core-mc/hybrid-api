package hybrid.api.mod.settings;

public class ModeSetting<T extends Enum<T>> extends Setting<T> {

    private final T[] modes;

    public ModeSetting(String name, String desc, T value) {
        super(name, desc, value);
        this.modes = value.getDeclaringClass().getEnumConstants();
    }

    public T getMode() {
        return value;
    }

    public void setMode(T value) {
        set(value);
    }

    public void cycle() {
        int nextIndex = (value.ordinal() + 1) % modes.length;
        set(modes[nextIndex]);
    }
}