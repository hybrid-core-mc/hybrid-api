package hybrid.api.mixin;

import hybrid.api.rendering.ScreenBounds;
import hybrid.api.shader.HueShader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(InGameHud.class)
public class IngameHudMixin {
    @Inject(method = "renderCrosshair",at = @At(value = "HEAD"), cancellable = true)
    public void renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        //        if (mc.currentScreen instanceof HybridScreen) ci.cancel();
    }
   /* @Inject(method = "renderCrosshair",at = @At(value = "HEAD"), cancellable = true)
    public void renderCrosshair(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (mc.currentScreen instanceof HybridScreen) ci.cancel();
    }*/

}
