package hybrid.api.ui.theme;

import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;

import java.awt.*;

public class DefaultTheme implements GuiTheme {


    @Override
    public void renderModsBackground(Quad quad) {
        HybridRenderer2D.fillQuad(quad, Color.BLUE);
    }
}
