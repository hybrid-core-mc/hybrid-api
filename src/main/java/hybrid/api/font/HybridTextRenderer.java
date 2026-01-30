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
    private static final List<HybridRenderText> renderQueue = new ArrayList<>();

    public static void addText(String text, FontStyle style, int size, int x, int y, Color color) {
        renderQueue.add(getTextRenderer(text, style, size, x, y, color));
    }

    public static void addText(HybridRenderText text) {
        renderQueue.add(text);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color) {
        return getTextRenderer(text, style, size,0,0, color);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, Color color, boolean shadow) {
        return getTextRenderer(text, style, size, 0, 0, color, shadow);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, int x, int y, Color color) {
        return getTextRenderer(text, style, size, x, y, color, false);
    }

    public static HybridRenderText getTextRenderer(String text, FontStyle style, int size, int x, int y, Color color, boolean shadow) {
        Font font = fromFont(style, size);
        return new HybridRenderText(text, x, y, font, color, shadow);
    }

    public static HybridRenderText getIconRenderer(String name, int x, int y, Color color) {
        SVGDocument svgDocument = svgCache.computeIfAbsent(name, n -> {
            try {
                URL svgUrl = Objects.requireNonNull(
                        HybridApi.class.getResource("/assets/hybrid-api/icon/" + n.concat(".svg")),
                        "Cannot find svg icon: " + n
                );
                return new SVGLoader().load(svgUrl);
            } catch (Exception e) {
                throw new RuntimeException("unable to load svg: " + n, e);
            }
        });

        return new HybridRenderText(x, y, svgDocument, color, false);
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
}
