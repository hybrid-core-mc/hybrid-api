package hybrid.api.theme;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Map;

public class ThemeManager {
    private static final Map<ThemeTarget, Color> themeColors = new EnumMap<>(ThemeTarget.class);
    private static Color defaultColor = Color.WHITE;

    public static void setDefaultColor(Color color) {
        defaultColor = color;
    }

    public static void register(ThemeTarget target, Color color) {
        themeColors.put(target, color);
    }

    public static Color get(ThemeTarget target) {
        return themeColors.getOrDefault(target, defaultColor);
    }

    public static void clear() {
        themeColors.clear();
    }

    public static void init() {
        setDefaultColor(new Color(40, 40, 40));

        register(ThemeTarget.MAIN_BG, new Color(18, 20, 28));
        register(ThemeTarget.SIDEBAR_BG, new Color(22, 25, 35));
        register(ThemeTarget.ACCENT, new Color(99, 102, 241));
    }
}