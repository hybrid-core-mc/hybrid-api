package hybrid.api.mixin;

import hybrid.api.font.HybridTextRenderer;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.ui.HybridScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
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

        Window window = mc.getWindow();
        boolean canRender =
                mc.isWindowFocused()
                        && window.getWidth() > 0
                        && window.getHeight() > 0;

        if (!canRender) {
            return;
        }

        ci.cancel();

        HybridRenderer.render();

        ScreenBounds bounds = screen.getBounds();
        context.enableScissor(bounds.getX(), bounds.getY(), bounds.getX() + screen.getBounds().getWidth(), screen.getBounds().getY() + screen.getBounds().getHeight());
        HybridTextRenderer.render(context);
        context.disableScissor();

        for (var consumer : HybridRenderer.CONTEXT_LIST) consumer.accept(context);



    }

}
