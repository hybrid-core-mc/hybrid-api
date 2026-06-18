package hybrid.api.ui.gui.parts;

import hybrid.api.mod.HybridMod;

public interface GuiPart {

    void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight);

}