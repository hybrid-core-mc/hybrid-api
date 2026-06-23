package hybrid.api.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import hybrid.api.util.font.fancy.FontRenderer;
import hybrid.api.util.texture.HybridTextureRenderer;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;draw(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V", shift = At.Shift.AFTER))
    private void render(GpuBufferSlice fogSlice, CallbackInfo ci) {
        FontRenderer.flushAll();
        HybridTextureRenderer.flushAll();
    }
}