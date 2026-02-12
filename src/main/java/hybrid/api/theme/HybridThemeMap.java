package hybrid.api.theme;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class HybridThemeMap {

    public static final Map<ThemeColorKey, Color> COLORS = new EnumMap<>(ThemeColorKey.class);


    static {
        COLORS.put(ThemeColorKey.backgroundColor, new Color(23, 24, 31));
        COLORS.put(ThemeColorKey.modsBackgroundColor, new Color(30, 34, 48));
        COLORS.put(ThemeColorKey.modBackgroundColor, new Color(27, 29, 42));
        COLORS.put(ThemeColorKey.modButtonOutlineColor, new Color(49, 54, 77));
        COLORS.put(ThemeColorKey.uiOutlineColor, new Color(49, 54, 77, 190));
    }

    public static Color get(ThemeColorKey key) {
        return COLORS.get(key);
    }

    public static void set(ThemeColorKey key, Color color) {
        COLORS.put(key, color);
    }

}