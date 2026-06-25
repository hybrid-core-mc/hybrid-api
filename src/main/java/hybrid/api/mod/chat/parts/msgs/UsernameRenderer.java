package hybrid.api.mod.chat.parts.msgs;

import hybrid.api.Main;
import hybrid.api.mod.chat.parts.ChatLayoutController;
import hybrid.api.util.render.Quad;

public class UsernameRenderer {
    public static void render(String username, float contentX, float startY, Quad textClipping) {
        Main.RENDERER.drawText(
                Main.getStyle(),
                username,
                contentX,
                startY,
                ChatLayoutController.getUsernameFontSize(),
                0xFFFFFFFF,
                textClipping
        );
    }
}