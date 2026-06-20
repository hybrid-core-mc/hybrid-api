package hybrid.api.mod.settings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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

    public String getName() {
        return name;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    public List<BuiltCategory> getChildren() {
        return children;
    }

    public BuiltCategory add(Setting<?> setting) {
        settings.add(setting);
        return this;
    }

    public BuiltCategory addBool(String name,String desc, boolean value) {
        return add(new BooleanSetting(name, desc,value));
    }

    public BuiltCategory addNumber(String name, String desc, float value, float min, float max) {
        return add(new NumberSetting(name, desc,value, min, max));
    }

    public BuiltCategory addColor(String name, String desc, Color value) {
        return add(new ColorSetting(name, desc, value));
    }

    public BuiltCategory end() {
        return parent != null ? parent : this;
    }
}