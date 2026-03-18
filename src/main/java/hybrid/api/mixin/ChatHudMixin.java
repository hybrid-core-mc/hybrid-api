package hybrid.api.mixin;

import hybrid.api.HybridApi;
import hybrid.api.event.ChatEvent;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), argsOnly = true)
    private Text addMessage(Text message) {

        ChatEvent event = new ChatEvent(message.getString());

        HybridApi.EVENT_BUS.post(event);

        if (!event.isOverride()) {
            return message;
        }

        return Text.literal(event.getFinalMessage()).setStyle(message.getStyle());
    }
}