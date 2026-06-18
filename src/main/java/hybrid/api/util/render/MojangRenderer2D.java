package hybrid.api.util.render;

import hybrid.api.util.shader.QuadShader;

import java.awt.*;

public class MojangRenderer2D {

    public static void fillQuad(Quad quad, Color color) {
        RenderContext.get().fill(quad.x, quad.y, quad.x + quad.width, quad.y + quad.height, color.getRGB());
    }

    public static void renderOutline(Quad quad, Color color) {
        RenderContext.get().renderOutline(quad.x, quad.y, quad.width, quad.height, color.getRGB());
    }

    public static void drawRoundRect(Quad quad, int radius, float outlineRadius, Color outlineColor, Color color,
                                     float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new QuadShader.State( RenderContext.get(),
                quad.x, quad.y, quad.width, quad.height, radius, outlineRadius, outlineColor.getRGB(), color.getRGB(), topLeftRadius,topRightRadius,bottomLeftRadius,bottomRightRadius
        ));
    }

    public static void drawRoundRect(Quad quad, int radius, float outlineRadius, Color outlineColor, Color color) {
        drawRoundRect(quad, radius, outlineRadius, outlineColor, color, radius, radius, radius, radius);
    }
}
