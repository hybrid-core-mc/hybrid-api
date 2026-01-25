package hybrid.api.rendering;

import java.awt.*;

public interface HybridRenderer2D {
    void drawQuad(ScreenBounds bounds, Color color);

    void drawQuad(ScreenBounds bounds, Color color, int radius);

    void drawCircle(ScreenBounds bounds, Color color);

}
