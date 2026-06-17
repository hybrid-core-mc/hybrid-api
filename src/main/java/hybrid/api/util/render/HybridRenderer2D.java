package hybrid.api.util.render;

import hybrid.api.util.HybridConfig;

import java.awt.*;

public class HybridRenderer2D {

    public static void fillQuad(Quad quad, Color color) {
        switch (HybridConfig.renderStack) {
            case MOJANG -> RenderContext.get().fill(quad.x, quad.y, quad.x + quad.width, quad.y + quad.height, color.getRGB());
            case CUSTOM -> {
            }
        }
    }

}
