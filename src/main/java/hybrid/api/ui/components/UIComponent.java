package hybrid.api.ui.components;

import hybrid.api.util.render.Quad;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;

public class UIComponent {
    Quad bounds;


    public Quad getBounds() {
        return bounds;
    }

    public void render(int mouseX, int mouseY, float tickDelta) {

    }

    public void keyReleased(@NotNull KeyEvent keyEvent) {
    }


    public void keyPressed(@NotNull KeyEvent keyEvent) {
    }

    public void mouseReleased(@NotNull MouseButtonEvent mouseButtonEvent) {

    }

    public void mouseClicked(@NotNull MouseButtonEvent mouseButtonEvent) {

    }

    public void mouseDragged(@NotNull MouseButtonEvent mouseButtonEvent) {

    }

    public void mouseScrolled(@NotNull MouseButtonEvent mouseButtonEvent) {

    }


}
