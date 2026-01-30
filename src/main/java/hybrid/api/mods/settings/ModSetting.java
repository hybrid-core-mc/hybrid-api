package hybrid.api.mods.settings;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class ModSetting<T> {

    protected final String name;
    protected T value;

    private Consumer<T> onChange;

    protected ModSetting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T get() {
        return value;
    }

    public void set(T newValue) {
        if (Objects.equals(value, newValue)) {
            return;
        }

        this.value = newValue;

        if (onChange != null) {
            onChange.accept(newValue);
        }
    }

    public ModSetting<T> onChange(Consumer<T> listener) {
        this.onChange = listener;
        return this;
    }

}
