package hybrid.api.mod.settings;

import java.awt.*;

public class ColorSetting extends Setting<Color> {
    public ColorSetting(String name, String desc, Color value) {
        super(name, desc, value);
    }

    @Override
    public Color get() {
        return super.get();
    }

    @Override
    public void set(Color value) {
        super.set(value);
    }
}
