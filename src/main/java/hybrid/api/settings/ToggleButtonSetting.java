package hybrid.api.settings;

public class ToggleButtonSetting extends Setting {
    boolean value;

    public ToggleButtonSetting(String name, boolean defaultValue) {
        super(name);
        this.value = defaultValue;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

}
