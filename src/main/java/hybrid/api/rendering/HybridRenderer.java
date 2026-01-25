package hybrid.api.rendering;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.nanovg.NVGColor;

import java.awt.*;

import static hybrid.api.HybridApi.mc;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL2.*;

public class HybridRenderer implements HybridRenderer2D {
    public static final HybridRenderer RENDERER_INSTANCE = new HybridRenderer();
    private static final NVGColor NVG_COLOR = NVGColor.create();
    private static long CONTEXT = -1L;

    public static void init() {
        CONTEXT = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES | NVG_DEBUG);
        if (CONTEXT == 0 || CONTEXT == -1L)
            throw new RuntimeException("couldnt init nvg context");

        nvgGlobalCompositeOperation(CONTEXT, NVG_SOURCE_OVER);
    }

    public static void render() {

        Framebuffer frameBuffer = mc.getFramebuffer();

        int frameBufferWidth = mc.getWindow().getFramebufferWidth();
        int frameBufferHeight = mc.getWindow().getFramebufferHeight();

        setup();

        GpuTexture color = frameBuffer.getColorAttachment();
        GpuTexture depth = frameBuffer.getDepthAttachment();

        assert color != null;


        GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, ((GlTexture) color).getOrCreateFramebuffer(((GlBackend) RenderSystem.getDevice()).getBufferManager(), depth));

        GlStateManager._viewport(0, 0, frameBufferWidth, frameBufferHeight);

        nvgBeginFrame(CONTEXT,
                mc.getWindow().getScaledWidth(),
                mc.getWindow().getScaledHeight(),
                1.0f
        );

        float scale = (float) frameBufferWidth / mc.getWindow().getWidth();
        nvgScale(CONTEXT, scale, scale);

        HybridRenderQueue.renderAll(RENDERER_INSTANCE);

        nvgEndFrame(CONTEXT);
        restore();

        restore();
    }

    private static void setup() {
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, GlConst.GL_ONE, GlConst.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager._disableDepthTest();
        GlStateManager._disableCull();
    }

    private static void restore() {
        GlStateManager._enableDepthTest();
        GlStateManager._enableCull();
        GlStateManager._disableBlend();
    }

    private static void setColor(long ctx, Color color) {
        NVG_COLOR.r(color.getRed() / 255f)
                .g(color.getGreen() / 255f)
                .b(color.getBlue() / 255f)
                .a(color.getAlpha() / 255f);

        nvgFillColor(ctx, NVG_COLOR);
    }

    @Override
    public void fillQuad(ScreenBounds bounds, Color color) {
        nvgBeginPath(CONTEXT);
        setColor(CONTEXT, color);
        nvgRoundedRect(CONTEXT, bounds.x, bounds.y, bounds.width, bounds.height, 5);
        nvgFill(CONTEXT);
    }


}
