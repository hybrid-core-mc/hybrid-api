package hybrid.api.mixin;

import hybrid.api.HybridApi;
import hybrid.api.config.HybridConfig;
import hybrid.api.event.TickEvent;
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
    @Inject(method = "tick",at = @At(value = "HEAD"))
    public void tick(CallbackInfo ci){
        HybridApi.EVENT_BUS.post(new TickEvent());
    }
}
