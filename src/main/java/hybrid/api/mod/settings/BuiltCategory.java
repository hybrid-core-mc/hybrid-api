package hybrid.api.mod.settings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BuiltCategory {

    private final String name;
    private final List<Setting<?>> settings = new ArrayList<>();
    private final List<BuiltCategory> children = new ArrayList<>();

    private BuiltCategory parent;

    public BuiltCategory(String name) {
        this.name = name;
    }

    
    public static BuiltCategory add(String name) {
        return new BuiltCategory(name);
    }

    
    public static BuiltCategory create(String name, Consumer<BuiltCategory> configurationBlock) {
        BuiltCategory category = new BuiltCategory(name);
        configurationBlock.accept(category);
        return category;
    }

    public String getName() {
        return name;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public List<BuiltCategory> getChildren() {
        return children;
    }

    
    @SuppressWarnings("unchecked")
    public <S extends Setting<?>> S add(S setting) {
        settings.add(setting);
        return setting;
    }

    
    public BuiltCategory addNumber(String name, String desc, float value, float min, float max) {
        add(new NumberSetting(name, desc, value, min, max));
        return this;
    }

    public BuiltCategory addBool(String name, String desc, boolean value) {
        add(new BooleanSetting(name, desc, value));
        return this;
    }

    
    public ColorSetting addColor(String name, String desc, Color value) {
        return add(new ColorSetting(name, desc, value));
    }

    public <T extends Enum<T>> ModeSetting<T> addMode(String name, String desc, T value) {
        return add(new ModeSetting<T>(name, desc, value));
    }

    public BuiltCategory end() {
        return parent != null ? parent : this;
    }
}