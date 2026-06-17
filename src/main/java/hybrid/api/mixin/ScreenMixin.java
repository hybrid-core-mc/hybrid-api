package hybrid.api.mixin;

import hybrid.api.ducks.GuiGraphicsAccessor;
import hybrid.api.util.render.RenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(Screen.class)
public class ScreenMixin {
    @Inject(method = "render",at = @At(value = "HEAD"))
    public void render(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci){
        RenderContext.begin(guiGraphics);
    }


}
