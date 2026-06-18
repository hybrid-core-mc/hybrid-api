package hybrid.api.ui.gui.parts;

import hybrid.api.mod.HybridMod;
import net.minecraft.client.input.MouseButtonEvent;

public interface GuiPart {

    void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight);
    void mouseClicked(MouseButtonEvent mouseButtonEvent);
    void mouser(MouseButtonEvent mouseButtonEvent);


}