package hybrid.api.rendering;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;

public class GuiFramebuffer extends Framebuffer implements AutoCloseable {

    private int lastWidth  = -1;
    private int lastHeight = -1;

    public GuiFramebuffer() {
        super("Hybrid GUI FBO", true);
        resizeToWindow();
    }

    public void resizeToWindow() {
        MinecraftClient mc = MinecraftClient.getInstance();
        resizeIfNeeded(
                mc.getWindow().getFramebufferWidth(),
                mc.getWindow().getFramebufferHeight()
        );
    }

    public boolean resizeIfNeeded(int fbW, int fbH) {
        if (fbW <= 0 || fbH <= 0) return false;

        if (fbW == lastWidth && fbH == lastHeight) {
            return false;
        }

        lastWidth  = fbW;
        lastHeight = fbH;

        resize(fbW, fbH);
        return true;
    }

    @Override
    public void close() {
        delete();
    }
}