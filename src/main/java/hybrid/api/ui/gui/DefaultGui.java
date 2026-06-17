package hybrid.api.ui.gui;

import dev.bsprout.brapi.client.BRender;
import hybrid.api.mod.HybridMod;
import hybrid.api.ui.GuiDesign;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.renderers.MojangRenderer2D;

import java.awt.*;

public class DefaultGui implements GuiDesign {


    @Override
    public void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight) {


        int w = (int) (screenWidth * 0.58);
        int h = (int) (screenHeight * 0.65);


        Quad background = new Quad((screenWidth - w) / 2, (screenHeight - h) / 2, w, h);

        // render the background and border
        MojangRenderer2D.fillQuad(background, new Color(0, 72, 255, 225));



        Quad sidebar = background.copy().setWidth((int) (w * 0.23));

        // render the dividers background
        MojangRenderer2D.fillQuad(sidebar, new Color(28, 28, 30, 120));

        MojangRenderer2D.renderOutline(background, new Color(23, 23, 23,255));

    }
}