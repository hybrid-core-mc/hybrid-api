package hybrid.api.event;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class ScreenRenderEvent {
    public Screen screen;
    public DrawContext context;


    public ScreenRenderEvent(Screen screen, DrawContext context) {
        this.screen = screen;
        this.context = context;
    }
}
