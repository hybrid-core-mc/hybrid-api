package hybrid.api.mixin;


import hybrid.api.mod.chat.CustomChatScreen;
import hybrid.api.util.texture.PlayerInfoAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Inject(method = "handleSystemChat", at = @At("TAIL"))
    private void onSystemChat(ClientboundSystemChatPacket packet, CallbackInfo ci) {

        Minecraft mc = Minecraft.getInstance();
        CustomChatScreen screen;

        if (mc.getConnection() == null || !(mc.screen instanceof CustomChatScreen)) {
            return;
        }
        screen = (CustomChatScreen) mc.screen;

        String text = packet.content().getString();

        List<PlayerInfo> infos =
                ((PlayerInfoAccessor)
                        mc.gui.getTabList())
                        .hybrid_api$playerInfo();

        boolean found = false;
        for (PlayerInfo info : infos) {

            String name = info.getProfile().name();

            if (name == null || name.isEmpty()) continue;

            if (text.contains(name)) {
                Component username = info.getTabListDisplayName();
                screen.submitMsg(username == null ? "Server" : username.getString(), packet.content(), info.getSkin());
                found = true;
                break;
            }
        }
        if(!found){
            System.out.println("we gout a servererr"+packet);
            screen.submitMsg("Server", packet.content(),null);
        }
    }

    @Inject(method = "handlePlayerChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/chat/ChatListener;handlePlayerChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/chat/ChatType$Bound;)V"))
    private void onPlayerChat(ClientboundPlayerChatPacket packet, CallbackInfo ci) {

    }

    @Inject(method = "handleDisguisedChat", at = @At("TAIL"))
    public void onDisguisedChat(ClientboundDisguisedChatPacket packet, CallbackInfo ci) {
        System.out.println("disguised chat POES");
    }
}