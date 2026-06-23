package hybrid.api.mixin;

import hybrid.api.util.texture.PlayerInfoAccessor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerTabOverlay.class)
public abstract class PlayerTabOverlayMixin implements PlayerInfoAccessor {
    @Shadow
    protected abstract List<PlayerInfo> getPlayerInfos();

    @Override
    public List<PlayerInfo> hybrid_api$playerInfo() {
        return getPlayerInfos();
    }
}
