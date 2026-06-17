package hybrid.api.ui;

import dev.bsprout.brapi.client.BRender;
import hybrid.api.mod.HybridMod;
import hybrid.api.mod.SprintMod;
import hybrid.api.ui.gui.DefaultGui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static hybrid.api.Main.mc;

public class HybridGuiScreen extends Screen {

    private final GuiDesign theme;
    private final HybridMod currentMod;
    private final BRender bRender = new BRender();

    public HybridGuiScreen() {
        super(Component.translatable("hybrid.gui"));

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