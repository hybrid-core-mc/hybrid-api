package hybrid.api.mixin;

import hybrid.api.rendering.HybridRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void init(MinecraftClient client, HeldItemRenderer firstPersonHeldItemRenderer, BufferBuilderStorage buffers, BlockRenderManager blockRenderManager, CallbackInfo ci) {
        HybridRenderer.init();
    }

}
