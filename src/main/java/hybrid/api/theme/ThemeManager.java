package hybrid.api.theme;

import java.awt.*;
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

    public static Color getColorWithAlpha(ThemeTarget target, int alhpa) {
        
        Color themeColor = get(target);

        
        float clampedAlpha = Math.max(0.0f, Math.min(1.0f, alhpa));

        return new Color(
                themeColor.getRed() / 255.0f,
                themeColor.getGreen() / 255.0f,
                themeColor.getBlue() / 255.0f,
                clampedAlpha
        );
    }

    public static void update(ThemeTarget target, Color newColor) {
        Color oldColor = themeColors.get(target);
        if (oldColor == null || !oldColor.equals(newColor)) {
            themeColors.put(target, newColor);
        }
    }
    public static void clear() {
        themeColors.clear();
    }

    public static void init() {
        setDefaultColor(new Color(40, 40, 40));

        register(ThemeTarget.MAIN_BG, new Color(18, 20, 28));
        register(ThemeTarget.SIDEBAR_BG, new Color(22, 25, 35));
        register(ThemeTarget.ACCENT, new Color(99, 102, 241));
        register(ThemeTarget.BORDER, new Color(44, 45, 56));
        register(ThemeTarget.ICONS_BG, new Color(13, 15, 20));
        register(ThemeTarget.ICON,Color.WHITE);
        register(ThemeTarget.COMP_BG, new Color(21, 23, 31));;
        register(ThemeTarget.COMP_SECONDARY, new Color(18, 20, 27));;
        register(ThemeTarget.TEXT_COLOR, Color.WHITE);
        register(ThemeTarget.TEXT_SECONDARY, Color.WHITE);
    }
}