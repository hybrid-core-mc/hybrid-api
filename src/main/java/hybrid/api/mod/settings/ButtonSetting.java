package hybrid.api.mod.settings;

import com.google.gson.JsonObject;
import hybrid.api.ui.components.settings.SettingComponent;

public class ButtonSetting extends ModSetting<Runnable> {
    String buttonName;
    public ButtonSetting(String name, Runnable defaultValue,String buttonName) {
        super(name, defaultValue);
        this.buttonName = buttonName;
    }


    @Override
    public void writeJson(JsonObject json) {

    }

    @Override
    public void readJson(JsonObject json) {

    }
}
