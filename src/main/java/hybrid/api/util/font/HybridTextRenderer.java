package hybrid.api.util.font;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import hybrid.api.Main;
import net.minecraft.client.gui.GuiGraphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.*;
import java.util.List;

public class HybridTextRenderer {

    private static final int MAX_CACHE_SIZE = 400;

    private static final Map<String, Font> fontCache = new HashMap<>();
    private static final Map<String, SVGDocument> svgCache = new HashMap<>();
    private static final Map<String, HybridRenderText> iconCache = new HashMap<>();

    
    public static final Map<String, HybridRenderText> textCache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, HybridRenderText> eldest) {
            if (size() > MAX_CACHE_SIZE) {
                if (eldest.getValue() != null && eldest.getValue().cachedTexture != null) {
                    eldest.getValue().cachedTexture.close();
                }
                return true;
            }
            return false;
        }
    };

    private static final BufferedImage METRIC_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    private static final Graphics2D METRIC_GRAPHICS = METRIC_IMAGE.createGraphics();
    private static final List<HybridRenderText> renderQueue = new ArrayList<>();

    public static int getCharWidth(char c, FontStyle style, int size) {

        Font font = fromFont(style, size);
        METRIC_GRAPHICS.setFont(font);
        return METRIC_GRAPHICS.getFontMetrics().charWidth(c) / 2;
    }

    public static int getStringWidth(String text, FontStyle style, int size) {

        if (text == null || text.isEmpty()) {
            return 0;
        }

        Font font = fromFont(style, size);

        METRIC_GRAPHICS.setFont(font);
        return METRIC_GRAPHICS.getFontMetrics().stringWidth(text) / 2;
    }

    public static void addText(String text, FontStyle style, int size, int x, int y, Color color) {
        renderQueue.add(getTextRenderer(text, style, size, x, y, color));
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color) {
        return getTextRenderer(text, style, size, 0, 0, color);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color, Color shadowColor, boolean shadow, int shadowRadius) {
        return getTextRenderer(text, style, size, 0, 0, color, shadowColor, shadow, shadowRadius);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color, Color shadowColor, boolean shadow) {
        return getTextRenderer(text, style, size, 0, 0, color, shadowColor, shadow, 1);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color, boolean shadow) {
        return getTextRenderer(text, style, size, 0, 0, color, new Color(84, 84, 84, 203), shadow, 1);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, int x, int y, Color color) {
        return getTextRenderer(text, style, size, x, y, color, null, false, 0);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, int x, int y, Color color, Color shadowColor, boolean shadow, int shadowRadius) {

        String key = text + "|" + style + "|" + size + "|" + color.getRGB() + "|" + shadow + "|" + shadowColor + "|" + shadowRadius;

        HybridRenderText cached = textCache.get(key);

        if (cached == null) {
            Font font = fromFont(style, size);
            cached = new HybridRenderText(text, 0, 0, font, shadowColor, color, shadow, shadowRadius);
            textCache.put(key, cached);
        }

        HybridRenderText renderer = new HybridRenderText(
                cached.text,
                x,
                y,
                cached.font,
                cached.shadowColor,
                cached.color,
                cached.shadow,
                cached.shadowRadius
        );

        renderer.cachedTexture = cached.cachedTexture;
        return renderer;
    }


    public static HybridRenderText getIconRenderer(String name, Color color, int x, int y) {
        
        String key = name + "|" + color.getRGB();

        HybridRenderText cachedIcon = iconCache.get(key);
        if (cachedIcon == null) {
            SVGDocument svgDocument = svgCache.computeIfAbsent(name, n -> {
                try {
                    URL svgUrl = Objects.requireNonNull(Main.class.getResource("/assets/hybrid-api/icon/" + n + ".svg"),
                            "Cannot find svg icon: " + n
                    );
                    return new SVGLoader().load(svgUrl);
                } catch (Exception e) {
                    throw new RuntimeException("unable to load svg: " + n, e);
                }
            });

            
            cachedIcon = new HybridRenderText(0, 0, svgDocument, color);
            iconCache.put(key, cachedIcon);
        }

        System.out.println(iconCache.size());
        
        HybridRenderText instance = new HybridRenderText(x, y, cachedIcon.getSvgDocument(), cachedIcon.color);
        instance.cachedTexture = cachedIcon.cachedTexture; 

        return instance;
    }

    public static HybridRenderText getIconRenderer(String name, Color color) {
        return getIconRenderer(name, color, 0, 0);
    }

    public static Font fromFont(FontStyle style, int size) {
        String key = style + "|" + size;

        return fontCache.computeIfAbsent(key, k -> {
            String basePath = "/assets/hybrid-api/font/";
            int awtStyle;

            String file = switch (style) {
                case REGULAR -> {
                    awtStyle = Font.PLAIN;
                    yield "inter-regular";
                }
                case BOLD -> {
                    awtStyle = Font.BOLD;
                    yield "inter-bold";
                }
                case EXTRABOLD -> {
                    awtStyle = Font.BOLD;
                    yield "inter-extra-bold";
                }
                case ITALIC -> {
                    awtStyle = Font.ITALIC;
                    yield "inter-italic";
                }
            };

            String path = basePath + file + ".ttf";

            try (var in = Objects.requireNonNull(
                    Main.class.getResourceAsStream(path),
                    "Font missing: " + path
            )) {
                return Font.createFont(Font.TRUETYPE_FONT, in)
                           .deriveFont(awtStyle, (float) size);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load font: " + path, e);
            }
        });
    }

    public static void render(GuiGraphics context) {
        for (HybridRenderText text : renderQueue) {
            text.draw(context);
        }
        renderQueue.clear();
    }

    public static void addText(HybridRenderText text) {
        if (text == null) return;
        renderQueue.add(text);
    }


    public static void reload() {
        
        for (HybridRenderText text : textCache.values()) {
            if (text != null && text.cachedTexture != null) {
                text.cachedTexture.close();
            }
        }
        textCache.clear();

        for (HybridRenderText icon : iconCache.values()) {
            if (icon != null && icon.cachedTexture != null) {
                icon.cachedTexture.close();
            }
        }
        iconCache.clear();

        fontCache.clear();
        svgCache.clear();
        renderQueue.clear();
    }
}