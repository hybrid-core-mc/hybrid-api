package hybrid.api.mixin;

import hybrid.api.rendering.ScreenBounds;
import hybrid.api.shader.HueShader;
import hybrid.api.ui.HybridScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(Screen.class)
public abstract class ScreenMixin {


    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {

        if (!((Object) this instanceof HybridScreen screen)) return;

        ci.cancel();


    }
}
