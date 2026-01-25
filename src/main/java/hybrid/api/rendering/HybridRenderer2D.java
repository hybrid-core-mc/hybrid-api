package hybrid.api.rendering;

import java.awt.*;

public interface HybridRenderer2D {
    void fillQuad(ScreenBounds bounds, Color color);

    void fillQuad(ScreenBounds bounds, Color color, int radius);
}
