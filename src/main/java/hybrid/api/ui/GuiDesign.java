package hybrid.api.ui;

import hybrid.api.mod.HybridMod;

import javax.swing.*;

public interface GuiDesign  {

    void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight);

}