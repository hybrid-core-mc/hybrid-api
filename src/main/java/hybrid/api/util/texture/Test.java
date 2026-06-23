package hybrid.api.util.texture;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static hybrid.api.Main.mc;

public class Test extends Screen {
    private final HybridTextureRenderer hybridTextureRenderer = new HybridTextureRenderer();
    private HybridTexture texture;

    private float followerX = 0, followerY = 0;
    private float targetX = 0, targetY = 0;
    private int scrollY = 0;


    public Test() {
        super(Component.literal("BRender Test Screen"));
    }

    @Override
    protected void init() {
        List<PlayerInfo> infos = ((PlayerInfoAccessor) mc.gui.getTabList()).hybrid_api$playerInfo();
        texture = new HybridTexture(Identifier.fromNamespaceAndPath("hybrid-api", "test.png"));
        followerX = this.width / 2f;
        followerY = this.height / 2f;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        targetX = (float) mouseX;
        targetY = (float) mouseY;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
        scrollY = Math.max(0, scrollY - (int) (vAmount * 30));
        return true;
    }


    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        float speed = 0.15f;
        followerX += (targetX - followerX) * speed;
        followerY += (targetY - followerY) * speed;

        int rowY = 50;
        List<PlayerInfo> infos = ((PlayerInfoAccessor) mc.gui.getTabList()).hybrid_api$playerInfo();

        if (!infos.isEmpty()) {
            PlayerInfo playerInfo = infos.get(0);


            HybridTexture skinTexture = TextureCache.getOrCreate(playerInfo.getSkin().body().texturePath());

            int headX = 5;
            int headY = 5;
            int headSize = 20;
            int colorFilter = 0xFFFFFFFF;


            hybridTextureRenderer.drawTextureSubRegion(
                    skinTexture,
                    headX, headY, headSize, headSize,
                    8, 8, 8, 8,
                    colorFilter, false
            );


            if (playerInfo.showHat()) {
                hybridTextureRenderer.drawTextureSubRegion(
                        skinTexture,
                        headX, headY, headSize, headSize,
                        40, 8, 8, 8,
                        colorFilter, false
                );
            }
        }


        hybridTextureRenderer.drawTexture(texture, 10, rowY, 500, 300, 0xFFFFFFFF, true);


        hybridTextureRenderer.flush();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        if (texture != null) texture.close();
    }
}