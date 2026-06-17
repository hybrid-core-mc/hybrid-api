package hybrid.api.ui.gui;

import hybrid.api.mod.HybridMod;
import hybrid.api.ui.GuiDesign;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.renderers.MojangRenderer2D;

import java.awt.*;

public class DefaultGui implements GuiDesign {

    @Override
    public void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight) {

        int w = (int) (screenWidth * 0.7);
        int h = (int) (screenHeight * 0.4);

        int x = (screenWidth - w) / 2;
        int y = (screenHeight - h) / 2;

        Quad quad = new Quad(x, y, w, h);

        MojangRenderer2D.fillQuad(quad, Color.BLUE);
    }
}