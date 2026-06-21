package hybrid.api.util.render;

import hybrid.api.util.shader.CircleShader;
import hybrid.api.util.shader.HueShader;
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

    public static void drawRoundRect(Quad quad, Color color, Color outlineColor, int radius, float outlineRadius, float borderStyle, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new QuadShader.State( RenderContext.get(),
                quad.x, quad.y, quad.width, quad.height, radius, outlineRadius, outlineColor.getRGB(), color.getRGB(), topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius, borderStyle
        ));
    }

    public static void drawRoundRect(Quad quad, Color color, Color outlineColor, int radius, float outlineRadius) {
        drawRoundRect(quad, color, outlineColor, radius, outlineRadius, 0, radius, radius, radius, radius);
    }

    public static void drawRoundRect(Quad quad, Color color, Color outlineColor, int radius, float outlineRadius, int borderStyle) {
        drawRoundRect(quad, color, outlineColor, radius, outlineRadius, borderStyle, radius, radius, radius, radius);
    }

    public static void drawRoundRect(Quad quad, Color color, Color outlineColor, int radius, float outlineRadius, float topLeftRadius, float topRightRadius, float bottomLeftRadius, float bottomRightRadius) {
        drawRoundRect(quad, color, outlineColor, radius, outlineRadius, 0, topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius);
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
    public static void drawTriangle(Quad quad, Color color,float angle) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new TriangleShader.State(
                RenderContext.get(),
                quad.x,
                quad.y,
                quad.width,
                quad.height,
                color.getRGB(),angle
        ));
    }
    public static void drawHueCircle(Quad quad,float radius, Color color) {
        RenderContext.get().guiRenderState.submitPicturesInPictureState(new HueShader.State(
                RenderContext.get(),
                quad.x,
                quad.y,
                radius,
                color.getRGB()
        ));
    }
}