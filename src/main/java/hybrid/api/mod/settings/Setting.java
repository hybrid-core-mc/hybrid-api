package hybrid.api.mod.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Setting<T> {

    protected String name,desc;
    protected T value;

    private final List<Consumer<T>> listeners = new ArrayList<>();

    public Setting(String name, String desc, T value) {
        this.name = name;
        this.desc = desc;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
        notifyListeners();
    }

    public Setting<T> onChange(Consumer<T> listener) {
        listeners.add(listener);
        return this;
    }

    protected void notifyListeners() {
        for (Consumer<T> l : listeners) {
            l.accept(value);
        }
    }
}