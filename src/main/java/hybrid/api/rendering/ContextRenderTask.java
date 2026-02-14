package hybrid.api.rendering;

import net.minecraft.client.gui.DrawContext;

public interface ContextRenderTask {
    void render(DrawContext context, HybridRenderer2D renderer);
}