package hybrid.api.ui.entry;

import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class HybridGuiScreen extends Screen {
    public HybridGuiScreen() {
        super(Component.translatable("gui.hybrid"));
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
        HybridRenderer2D.fillQuad(new Quad(mouseX, mouseY, mouseX + 67, mouseY + 67), Color.BLUE);
    }
}
