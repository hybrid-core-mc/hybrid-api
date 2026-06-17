package hybrid.api.util.render.renderers;

import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;

import java.awt.*;

public class MojangRenderer2D {

    public static void fillQuad(Quad quad, Color color) {
        RenderContext.get().fill(quad.x, quad.y, quad.x + quad.width, quad.y + quad.height, color.getRGB());
    }

    public static void renderOutline(Quad quad, Color color) {
        RenderContext.get().renderOutline(quad.x, quad.y, quad.width, quad.height, color.getRGB());
    }
}
