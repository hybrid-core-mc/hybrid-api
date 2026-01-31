package hybrid.api.font;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.parser.SVGLoader;
import hybrid.api.HybridApi;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;

public class HybridTextRenderer {

    private static final Map<String, Font> fontCache = new HashMap<>();
    private static final Map<String, SVGDocument> svgCache = new HashMap<>();
    private static final Map<String, HybridRenderText> textCache = new HashMap<>();
    private static final Map<String, HybridRenderText> iconCache = new HashMap<>();

    private static final List<HybridRenderText> renderQueue = new ArrayList<>();



    public static void addText(String text, FontStyle style, int size, int x, int y, Color color) {
        renderQueue.add(getTextRenderer(text, style, size, x, y, color));
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color) {
        return getTextRenderer(text, style, size, 0, 0, color);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color, Color shadowColor, boolean shadow) {
        return getTextRenderer(text, style, size, 0, 0, color, shadowColor, shadow);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color, boolean shadow) {
        return getTextRenderer(text, style, size, 0, 0, color, new Color(84, 84, 84, 203), shadow);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, int x, int y, Color color) {
        return getTextRenderer(text, style, size, x, y, color, null, false);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, int x, int y, Color color, Color shadowColor, boolean shadow) {

        String key = text + "|" + style + "|" + size + "|" + color.getRGB() + "|" + shadow + "|" + shadowColor;

        HybridRenderText renderer = textCache.get(key);

        if (renderer == null) {
            Font font = fromFont(style, size);
            renderer = new HybridRenderText(text, 0, 0, font,shadowColor, color, shadow);
            textCache.put(key, renderer);
        }

        renderer.setPosition(x, y);
        return renderer;
    }


    public static HybridRenderText getIconRenderer(String name, Color color) {

        String key = name + "|" + color.getRGB();

        HybridRenderText icon = iconCache.get(key);
        if (icon != null) {
            return icon;
        }

        SVGDocument svgDocument = svgCache.computeIfAbsent(name, n -> {
            try {
                URL svgUrl = Objects.requireNonNull(HybridApi.class.getResource("/assets/hybrid-api/icon/" + n + ".svg"),
                        "Cannot find svg icon: " + n
                );
                return new SVGLoader().load(svgUrl);
            } catch (Exception e) {
                throw new RuntimeException("unable to load svg: " + n, e);
            }
        });

        icon = new HybridRenderText(0, 0, svgDocument, color, false);
        iconCache.put(key, icon);

        return icon;
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
                    HybridApi.class.getResourceAsStream(path),
                    "Font missing: " + path
            )) {
                return Font.createFont(Font.TRUETYPE_FONT, in)
                           .deriveFont(awtStyle, (float) size);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load font: " + path, e);
            }
        });
    }


    public static void render(DrawContext context) {
        for (HybridRenderText text : renderQueue) {
            text.draw(context);
        }
        renderQueue.clear();
    }

    public static void addText(HybridRenderText text) {
        if (text == null) return;
        renderQueue.add(text);
    }

}
