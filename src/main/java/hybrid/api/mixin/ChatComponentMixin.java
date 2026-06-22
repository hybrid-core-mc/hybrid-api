package hybrid.api.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(method = "addMessageToQueue",at = @At(value = "HEAD"))
    public void addmessage(GuiMessage guiMessage, CallbackInfo ci){
        System.out.println("added mesage"+guiMessage.content());
        Component component = guiMessage.content();
        System.out.println("added magic "+component.getString());
    }
}
