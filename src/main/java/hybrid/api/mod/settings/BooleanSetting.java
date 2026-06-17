package hybrid.api.mod.settings;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, boolean value) {
        super(name, value);
    }

    public boolean isEnabled() {
        return value;
    }

    public void toggle() {
        set(!value);
    }
}