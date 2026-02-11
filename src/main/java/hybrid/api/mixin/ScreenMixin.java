package hybrid.api.mixin;

import hybrid.api.font.HybridTextRenderer;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.HybridScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {


    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        if (!((Object) this instanceof HybridScreen screen)) return;

        ci.cancel();
        ScreenBounds bounds = screen.getBounds();
        context.enableScissor(bounds.getX(), bounds.getY(), bounds.getX() + screen.getBounds().getWidth(), screen.getBounds().getY() + screen.getBounds().getHeight());
        HybridTextRenderer.render(context);
        context.disableScissor();

        for (var consumer : HybridRenderer.CONTEXT_LIST) consumer.accept(context);



    }

}
