package hybrid.api.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

public final class GuiFramebuffer extends Framebuffer implements AutoCloseable {

    public GuiFramebuffer() {
        super("Hybrid GUI FBO", true);
        resizeToWindow();
    }

    public void resizeToWindow() {
        MinecraftClient mc = MinecraftClient.getInstance();
        resize(
                mc.getWindow().getFramebufferWidth(),
                mc.getWindow().getFramebufferHeight()
        );
    }

    @Override
    public void close() {
        delete();
    }
}