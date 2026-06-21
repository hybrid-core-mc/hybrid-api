package hybrid.api.ui;

import hybrid.api.mod.HybridMod;
import hybrid.api.mod.SprintMod;
import hybrid.api.ui.gui.normal.DefaultGui;
import hybrid.api.ui.gui.parts.GuiPart;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.RenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static hybrid.api.Main.mc;

public class HybridGuiScreen extends Screen {

    private final GuiPart gui;
    private final HybridMod currentMod;

    public HybridGuiScreen() {
        super(Component.translatable("hybrid.gui"));

        this.gui = new DefaultGui();
        this.currentMod = new SprintMod();
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) {

        super.render(graphics, mouseX, mouseY, tickDelta);


        assert mc.screen != null;
        gui.render(mouseX, mouseY, tickDelta, currentMod, mc.screen.width, mc.screen.height);
    }


    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        gui.mouseScrolled(d, e, f, g);
        return super.mouseScrolled(d, e, f, g);
    }

    @Override
    public boolean mouseDragged(@NotNull MouseButtonEvent mouseButtonEvent, double d, double e) {
        gui.mouseDragged(mouseButtonEvent);
        return super.mouseDragged(mouseButtonEvent, d, e);
    }

    @Override
    public boolean mouseReleased(@NotNull MouseButtonEvent mouseButtonEvent) {
        gui.mouseReleased(mouseButtonEvent);
        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        gui.mouseClicked(mouseButtonEvent);
        return super.mouseClicked(mouseButtonEvent, bl);
    }
}