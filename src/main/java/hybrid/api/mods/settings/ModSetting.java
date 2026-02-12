package hybrid.api.mods.settings;

import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public abstract class ModSetting<T> {

    protected final String name;
    protected T value;

    private Consumer<T> onChange;

    private BooleanSupplier visibleSupplier = () -> true;

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
        if (Objects.equals(value, newValue)) return;

        this.value = newValue;
        if (onChange != null)
            onChange.accept(newValue);
    }

    public ModSetting<T> onChange(Consumer<T> listener) {
        this.onChange = listener;
        return this;
    }

    public BooleanSupplier getVisibleSupplier() {
        return visibleSupplier;
    }

    public ModSetting<T> visible(BooleanSupplier supplier) {
        this.visibleSupplier = supplier;
        return this;
    }


    public abstract void writeJson(JsonObject json);

    public abstract void readJson(JsonObject json);
}