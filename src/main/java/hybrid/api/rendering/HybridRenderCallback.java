package hybrid.api.rendering;

import net.minecraft.client.gui.DrawContext;

public interface HybridRenderCallback {
    void render(DrawContext context, HybridRenderer renderer);
}