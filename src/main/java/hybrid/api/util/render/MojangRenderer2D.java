package hybrid.api.util.render;

import java.awt.*;

public class MojangRenderer2D {

    public static void fillQuad(Quad quad, Color color) {
        RenderContext.get().fill(quad.x, quad.y, quad.x + quad.width, quad.y + quad.height, color.getRGB());
    }

    public static void renderOutline(Quad quad, Color color) {
        RenderContext.get().renderOutline(quad.x, quad.y, quad.width, quad.height, color.getRGB());
    }

    public static void drawRoundRect(Quad quad, float radius, Color color) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new QuadShader.State( RenderContext.get(),
                quad.x, quad.y, quad.width, quad.height, radius, color.getRGB()
        ));
    }

}
