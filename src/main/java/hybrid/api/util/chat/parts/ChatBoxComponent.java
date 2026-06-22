package hybrid.api.util.chat.parts;

import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;

import java.awt.*;

public class ChatBoxComponent {
    public ChatBoxComponent() {

    }

    public void render(Quad quad, int mouseX, int mouseY) {

        HybridRenderer2D.drawRoundRect(quad, new Color(1,1,1,150),Color.RED,8,0);

    }

}
