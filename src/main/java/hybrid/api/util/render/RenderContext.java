package hybrid.api.util.render;

import net.minecraft.client.gui.GuiGraphics;

public class RenderContext {
    private static GuiGraphics current;

    public static void begin(GuiGraphics guiGraphics) {
        current = guiGraphics;
    }

    public static GuiGraphics get() {
        return current;
    }
}