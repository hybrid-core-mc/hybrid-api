package hybrid.api.mod;

import hybrid.api.mod.settings.BuiltCategory;
import hybrid.api.theme.ThemeManager;
import hybrid.api.theme.ThemeTarget;

import java.awt.*;

public class ThemeMod extends HybridMod {
    public ThemeMod() {
        super("Themes", "Change GUI Layout and colors", 1.0f);
    }

    @Override
    public void onInitialize() {

    }

    @Override
    public void onSetupSettings() {

        registerCategory(BuiltCategory.create("Colors", cat -> {
            var targetSetting = cat.addMode("Color Target", "Target part of the GUI", ThemeTarget.ACCENT);
            var colorSetting = cat.addColor("Color Value", "Color value for target", Color.PINK);




        }));

        registerCategory(BuiltCategory.create("Layout", cat -> {
            cat.addNumber("Width", "Width scale", 1.2f, 1.0f, 2.0f)
               .addNumber("Height", "Height scale", 1.2f, 1.0f, 2.0f);
        }));
    }
}
