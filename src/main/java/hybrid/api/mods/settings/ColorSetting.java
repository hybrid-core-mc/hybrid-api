package hybrid.api.mods.settings;

import org.spongepowered.asm.util.Locals;

import java.awt.*;

public class ColorSetting extends ModSetting<Color> {
    public ColorSetting(String name, Color defaultValue) {
        super(name, defaultValue);
    }
}
