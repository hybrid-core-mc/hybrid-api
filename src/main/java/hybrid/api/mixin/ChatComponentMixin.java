package hybrid.api.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static hybrid.api.Main.mc;

@Mixin(ChatComponent.class)
public class ChatComponentMixin {
    @Inject(method = "addMessageToQueue", at = @At("HEAD"))
    public void addmessage(GuiMessage guiMessage, CallbackInfo ci) {

        GuiMessageTag tag = guiMessage.tag();



        System.out.println("TAG: " + tag);

        
        String s = String.valueOf(tag);

        System.out.println("tag "+s);
    }
}
