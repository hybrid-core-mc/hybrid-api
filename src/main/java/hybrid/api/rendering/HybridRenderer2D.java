package hybrid.api.rendering;

import java.awt.*;

public interface HybridRenderer2D {
    void drawQuad(ScreenBounds bounds, Color color);

    void drawQuad(ScreenBounds bounds, Color color, int radius);

    void drawOutlineQuad(ScreenBounds bounds, Color fill, Color outline, int radius, int outlineRadius);

    void drawCircle(ScreenBounds bounds, Color color);

    void drawHorizontalLine(ScreenBounds bounds, Color colorStart, float distance);

    void beginScissors(ScreenBounds bounds);
    void endScissors();
    void drawColorTriangle(ScreenBounds bounds ,float hue,float padding);
    void drawAlphaSlider(ScreenBounds bounds, Color color);
}
