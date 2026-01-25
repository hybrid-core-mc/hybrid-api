package hybrid.api.screen.components;

import net.minecraft.client.gui.DrawContext;

public interface ScreenComponent {
    void render(DrawContext context, int mouseX, int mouseY, float deltaTicks);
}
