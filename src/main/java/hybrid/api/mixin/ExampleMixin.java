package hybrid.api.mixin;

import hybrid.api.rendering.HybridRenderQueue;
import hybrid.api.rendering.HybridRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static hybrid.api.HybridApi.mc;

@Mixin(Screen.class)
public class ExampleMixin {

	@Inject(method = "render",at = @At(value = "HEAD"))
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci){
	}
}