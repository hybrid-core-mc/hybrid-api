package hybrid.api.mixin;

import hybrid.api.rendering.HybridRenderQueue;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "render", at = @At(value = "TAIL"))
    public void testing(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {


    }
}
