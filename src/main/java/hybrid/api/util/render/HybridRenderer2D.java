package hybrid.api.util.render;

import hybrid.api.util.shader.CircleShader;
import hybrid.api.util.shader.QuadShader;
import hybrid.api.util.shader.TriangleShader;

import java.awt.*;

public class HybridRenderer2D {




    public static void fillQuad(Quad quad, Color color) {
        RenderContext.get().fill(quad.x, quad.y, quad.x + quad.width, quad.y + quad.height, color.getRGB());
    }

    public static void renderOutline(Quad quad, Color color) {
        RenderContext.get().renderOutline(quad.x, quad.y, quad.width, quad.height, color.getRGB());
    }

    public static void drawRoundRect(Quad quad, int radius, float outlineRadius, Color outlineColor, Color color, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius, float borderStyle) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new QuadShader.State( RenderContext.get(),
                quad.x, quad.y, quad.width, quad.height, radius, outlineRadius, outlineColor.getRGB(), color.getRGB(), topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius, borderStyle
        ));
    }

    public static void drawRoundRect(Quad quad, int radius, float outlineRadius, Color outlineColor, Color color) {
        drawRoundRect(quad, radius, outlineRadius, outlineColor, color, radius, radius, radius, radius, 0);
    }

    public static void drawRoundRect(Quad quad, int radius, float outlineRadius, Color outlineColor, Color color, int borderStyle) {
        drawRoundRect(quad, radius, outlineRadius, outlineColor, color, radius, radius, radius, radius, borderStyle);
    }

    public static void drawRoundRect(Quad quad, int radius, float outlineRadius, Color outlineColor, Color color, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        drawRoundRect(quad, radius, outlineRadius, outlineColor, color, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius, 0);
    }

    public static void drawCircle(Quad quad, float radius, Color color, boolean glow) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new CircleShader.State(
                RenderContext.get(),
                quad.x,
                quad.y,
                quad.width,
                quad.height,
                radius,
                color.getRGB(), glow
        ));
    }
    public static void drawTriangle(Quad quad, Color color) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new TriangleShader.State(
                RenderContext.get(),
                quad.x,
                quad.y,
                quad.width,
                quad.height,
                color.getRGB()
        ));
    }

}