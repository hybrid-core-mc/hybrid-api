package hybrid.api.ui.entry;

import hybrid.api.ui.theme.DefaultTheme;
import hybrid.api.ui.theme.GuiTheme;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class HybridGuiScreen extends Screen {

    GuiTheme guiTheme;
    ModContent content;
    ModController controller;

    public HybridGuiScreen() {
        super(Component.translatable("gui.hybrid"));

        guiTheme = new DefaultTheme();
        content = new ModContent(guiTheme);
        controller = new ModController(guiTheme);

    }
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        super.render(guiGraphics, mouseX, mouseY, tickDelta);
        content.render(mouseX, mouseY, tickDelta);
    }
}
