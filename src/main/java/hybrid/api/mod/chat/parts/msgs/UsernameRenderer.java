package hybrid.api.mod.chat.parts.msgs;

import hybrid.api.Main;
import hybrid.api.mod.chat.parts.ChatLayoutController;
import hybrid.api.util.render.Quad;

import java.awt.*;

public class UsernameRenderer {
    public static void render(String username, float contentX, float startY, Quad textClipping,int alhpa) {
        Main.RENDERER.drawText(
                Main.getStyle(),
                username,
                contentX,
                startY,
                ChatLayoutController.getUsernameFontSize(),
                new Color(255, 255, 255,alhpa).getRGB(),0,
                textClipping
        );
    }
}