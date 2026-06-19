package hybrid.api.mod.settings;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name,String desc, boolean value) {
        super(name, desc,value);
    }

    public boolean isEnabled() {
        return value;
    }

    public void toggle() {
        set(!value);
    }
}