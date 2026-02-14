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

import static hybrid.api.HybridApi.mc;

@Mixin(Screen.class)
public abstract class ScreenMixin {


    @Inject(method = "renderBackground", at = @At("HEAD"), cancellable = true)
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {

        if (!((Object) this instanceof HybridScreen screen)) return;

        ci.cancel();

        ScreenBounds bounds = screen.getBounds();
        context.enableScissor(
                bounds.getX(),
                bounds.getY(),
                bounds.getX() + bounds.getWidth(),
                bounds.getY() + bounds.getHeight()
        );

        HybridTextRenderer.render(context);

        for (var consumer : HybridRenderer.CONTEXT_LIST) {
            consumer.render(context, HybridRenderer.RENDERER_INSTANCE);
        }

        HybridRenderer.CONTEXT_LIST.clear();

        context.disableScissor();

        context.drawText(mc.textRenderer, "size " + HybridRenderer.CONTEXT_LIST.size(), 5, 5, -1, true);
    }
}
