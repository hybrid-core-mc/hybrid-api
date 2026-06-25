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

    
    public BuiltCategory add(Setting<?> setting) {
        this.settings.add(setting);
        return this;
    }

    

    public BuiltCategory addNumber(String name, String desc, float value, float min, float max) {
        this.settings.add(new NumberSetting(name, desc, value, min, max));
        return this;
    }

    public BuiltCategory addBool(String name, String desc, boolean value) {
        this.settings.add(new BooleanSetting(name, desc, value));
        return this;
    }

    public BuiltCategory addColor(String name, String desc, Color value) {
        this.settings.add(new ColorSetting(name, desc, value));
        return this;
    }

    public <T extends Enum<T>> BuiltCategory addMode(String name, String desc, T value) {
        this.settings.add(new ModeSetting<T>(name, desc, value));
        return this;
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

    public BuiltCategory end() {
        return parent != null ? parent : this;
    }
}