package hybrid.api.ui.theme;

import hybrid.api.util.render.renderers.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.renderers.MojangRenderer2D;

import java.awt.*;

public class DefaultTheme implements GuiTheme {

    @Override
    public void renderModsBackground(Quad quad) {
        MojangRenderer2D.fillQuad(quad, Color.BLUE);
    }

    @Override
    public void renderSidePanel(Quad quad) {
        MojangRenderer2D.fillQuad(quad, Color.RED);
    }
}
