package hybrid.api.mixin;

import hybrid.api.util.render.RenderContext;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static hybrid.api.Main.mc;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "render", at = @At(value = "HEAD"))
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (mc.screen != null) return;
        RenderContext.begin(guiGraphics);
    }
}
