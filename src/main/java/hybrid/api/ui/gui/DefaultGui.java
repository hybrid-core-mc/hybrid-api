package hybrid.api.ui.gui;

import hybrid.api.mod.HybridMod;
import hybrid.api.ui.GuiDesign;
import hybrid.api.util.render.MojangRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;

import java.awt.*;

import static hybrid.api.Main.mc;

public class DefaultGui implements GuiDesign {


    @Override
    public void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight) {


        int w = (int) (screenWidth * 0.58);
        int h = (int) (screenHeight * 0.65);


        Quad background = new Quad((screenWidth - w) / 2, (screenHeight - h) / 2, w, h);

        MojangRenderer2D.drawRoundRect(background, 10, 1.5f, new Color(29, 30, 37), new Color(18, 20, 28));

        MojangRenderer2D.drawRoundRect(background.copy().subtractWidth((int) (background.getWidth() * 0.75)),
                10, 1.5f, new Color(29, 30, 37), new Color(22, 25, 35),
                new float[]{10, 0, 10, 0});

        RenderContext.get().drawString(mc.font, "poes", 20, 20, new Color(255, 255, 255).getRGB());



    }
}