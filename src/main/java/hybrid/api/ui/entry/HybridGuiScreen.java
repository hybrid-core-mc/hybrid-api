package hybrid.api.ui.entry;

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
    public void render(@NotNull GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.fill(5,5,100,100, Color.RED.getRGB());
    }
}
