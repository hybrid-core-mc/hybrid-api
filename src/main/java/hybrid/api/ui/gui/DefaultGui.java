package hybrid.api.ui.gui;

import hybrid.api.mod.HybridMod;
import hybrid.api.ui.GuiDesign;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.MojangRenderer2D;

import java.awt.*;

public class DefaultGui implements GuiDesign {


    @Override
    public void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight) {


        int w = (int) (screenWidth * 0.58);
        int h = (int) (screenHeight * 0.65);


        Quad background = new Quad((screenWidth - w) / 2, (screenHeight - h) / 2, w, h);

        MojangRenderer2D.drawRoundRect(background, 8,new Color(22,25,35, 225));




    }
}