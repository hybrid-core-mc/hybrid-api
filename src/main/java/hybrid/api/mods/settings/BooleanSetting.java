package hybrid.api.mods.settings;

public class BooleanSetting extends ModSetting<Boolean> {

    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        set(!get());
    }

}
