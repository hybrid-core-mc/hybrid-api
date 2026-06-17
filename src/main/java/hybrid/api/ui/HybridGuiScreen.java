package hybrid.api.ui;

import hybrid.api.mod.HybridMod;
import hybrid.api.mod.SprintMod;
import hybrid.api.ui.gui.DefaultGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static hybrid.api.Main.mc;

public class HybridGuiScreen extends Screen {

    private final GuiDesign theme;
    private final HybridMod currentMod;

    public HybridGuiScreen() {
        super(Component.translatable("gui.hybrid"));

        this.theme = new DefaultGui();
        this.currentMod = new SprintMod();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) {

        super.render(graphics, mouseX, mouseY, tickDelta);

        assert mc.screen != null;
        theme.render(mouseX, mouseY, tickDelta, currentMod, mc.screen.width, mc.screen.height);
    }
}