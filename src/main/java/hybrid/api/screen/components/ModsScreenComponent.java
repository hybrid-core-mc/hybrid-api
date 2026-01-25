package hybrid.api.screen.components;

import hybrid.api.rendering.ScreenBounds;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class ModsScreenComponent implements ScreenComponent {
    ScreenBounds screenBounds;
    List<String> mods;

    public ModsScreenComponent(ScreenBounds screenBounds, List<String> mods) {
        this.screenBounds = screenBounds;
        this.mods = mods;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int maxWidth = 0;
        for (String mod : mods) {
        }
    }


}
