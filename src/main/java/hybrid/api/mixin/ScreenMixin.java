package hybrid.api.mixin;

import hybrid.api.font.HybridTextRenderer;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.screen.HybridScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public class ScreenMixin {

    @Inject(method = "renderBackground", at = @At(value = "HEAD"), cancellable = true)
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if ((Object) this instanceof HybridScreen) {
            ci.cancel();
            HybridRenderer.render();
            HybridTextRenderer.render(context);
            HybridRenderer.CONTEXT_LIST.forEach(consumer -> consumer.accept(context)); /// juciy gui sandwich i cant lie
        }
    }
}
