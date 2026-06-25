package hybrid.api.mod.chat.parts;

import hybrid.api.mod.ChatMod;
import hybrid.api.util.render.Quad;
import hybrid.api.util.texture.HybridTexture;
import hybrid.api.util.texture.HybridTextureRenderer;
import hybrid.api.util.texture.TextureCache;
import net.minecraft.world.entity.player.PlayerSkin;

public class AvatarRenderer {


    public static void render(HybridTextureRenderer renderer, float startX, float startY, PlayerSkin skin, Quad clipping) {


        HybridTexture skinTexture = TextureCache.getOrCreate(skin.body().texturePath());
        float avatarX = ChatLayoutController.getAvatarX(startX);
        
        int size = ChatLayoutController.getHeadSize();

        renderer.drawTextureSubRegion(
                skinTexture,
                avatarX,
                startY,
                size,
                size,
                8, 8, 8, 8,
                0xFFFFFFFF,
                1f,
                false
        );

        renderer.setClip(clipping);
        renderer.flush();
    }
}