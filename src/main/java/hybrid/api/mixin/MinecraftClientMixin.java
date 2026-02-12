package hybrid.api.mixin;

import hybrid.api.config.HybridConfig;
import hybrid.api.ui.HybridScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void init(RunArgs args, CallbackInfo ci) {
        HybridConfig.init();
    }
}
