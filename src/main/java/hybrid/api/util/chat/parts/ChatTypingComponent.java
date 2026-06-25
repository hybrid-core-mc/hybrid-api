package hybrid.api.util.chat.parts;

import hybrid.api.Main;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import hybrid.api.util.texture.PlayerInfoAccessor;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;

import static hybrid.api.Main.mc;

public class ChatTypingComponent {

    private final StringBuilder currentText = new StringBuilder();
    private final ChatTextComponent historyComponent;
    private Quad textInputQuad;

    private static final float FONT_SIZE = 11f;

    
    public ChatTypingComponent(ChatTextComponent historyComponent) {
        this.historyComponent = historyComponent;
    }

    public void render(Quad quad) {
        int buttonSpacing = 5;
        int buttonWidth = 28;

        int btnX = quad.getX() + quad.getWidth() - buttonWidth;
        int btnY = quad.getY();
        int btnH = quad.getHeight();
        Quad buttonQuad = new Quad(btnX, btnY, buttonWidth, btnH);

        int inputWidth = quad.getWidth() - buttonWidth - buttonSpacing;
        textInputQuad = new Quad(quad.getX(), quad.getY(), inputWidth, quad.getHeight());

        
        HybridRenderer2D.drawRoundRect(textInputQuad, new Color(101, 98, 98, 124), Color.RED, 8, 0);

        
        String textToDraw = currentText.toString();
        if (System.currentTimeMillis() % 1000 < 500) {
            textToDraw += "|";
        } else {
            textToDraw += " ";
        }

        float textY = textInputQuad.getY() + ((float) textInputQuad.getHeight() / 2f) - (FONT_SIZE / 2f);
        Quad clippy = textInputQuad.copy().addX(5).subtractWidth(10);

        
        Main.RENDERER.drawText(
                Main.getStyle(),
                textToDraw,
                textInputQuad.getX() + 6,
                textY,
                FONT_SIZE,
                -1, clippy
        );

        
        HybridRenderer2D.drawRoundRect(buttonQuad, new Color(255, 255, 255, 140), Color.RED, 8, 0);
        HybridRenderText dots = HybridTextRenderer.getIconRenderer("dots", Color.BLACK);
        dots.setPosition(btnX + buttonWidth / 2 - dots.getWidth() / 2, btnY + buttonWidth / 2 - (dots.getHeight() + 6) / 2);
        HybridTextRenderer.addText(dots);
        HybridTextRenderer.render(RenderContext.get());
    }

    public void charTyped(CharacterEvent characterEvent) {
        char codePoint = (char) characterEvent.codepoint();
        if (codePoint >= 32 && codePoint != 127) {
            currentText.append(codePoint);
        }
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.key() == GLFW.GLFW_KEY_BACKSPACE) {
            if (!currentText.isEmpty()) {
                currentText.deleteCharAt(currentText.length() - 1);
            }
        }

        if (keyEvent.key() == GLFW.GLFW_KEY_ENTER || keyEvent.key() == GLFW.GLFW_KEY_KP_ENTER) {
            String messageToSend = currentText.toString().trim();

            if (!messageToSend.isEmpty()) {

                List<PlayerInfo> playerInfos = ((PlayerInfoAccessor) mc.gui.getTabList()).hybrid_api$playerInfo();

                PlayerInfo info = null;
                for (PlayerInfo playerInfo : playerInfos) {
                    if(playerInfo.getProfile().equals(mc.player.getGameProfile())) info = playerInfo;
                }


                assert mc.player != null;
                if(info != null) {
                    historyComponent.submitMessage(mc.player.getGameProfile().name(),messageToSend, mc.player.getSkin());
                } else System.out.println("playeri skin is bull");

                clearText();
            }
        }
    }

    public String getText() {
        return currentText.toString();
    }

    public void clearText() {
        currentText.setLength(0);
    }
}