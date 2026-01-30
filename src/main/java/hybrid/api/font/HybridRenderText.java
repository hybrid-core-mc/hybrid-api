package hybrid.api.font;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.texture.TextureSetup;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class HybridRenderText {

    // wrirtten fully by spalshani this is really optimized cant get any better actually wrote this because nvg doesnt work on mc 1.21.10+ (sadly)

    String text;
    int x, y;
    Color color;
    Font font;
    boolean shadow;

    private HybridFontTexture cachedTexture;

    public HybridRenderText(String text, int x, int y, Font font, Color color, boolean shadow) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.color = color;
        this.shadow = shadow;
        this.cachedTexture = HybridFontTexture.createGlyph(this, text, shadow);
    }



    public Font getFont() {
        return font;
    }

    public void draw(DrawContext context) {
        if (cachedTexture == null || !cachedTexture.text().equals(text)) {
            cachedTexture = HybridFontTexture.createGlyph(this, text, shadow);
        }

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();


        matrices.translate(x, y);

        matrices.scale(0.5f, 0.5f); // todo : why does this work??

        context.state.addSimpleElement(new TexturedQuadGuiElementRenderState(
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.of(cachedTexture.texture().getGlTextureView(),
                        RenderSystem.getSamplerCache().get(FilterMode.LINEAR)),
                new Matrix3x2f(matrices),
                0, 0,
                cachedTexture.rectangle().width, cachedTexture.rectangle().height,
                0f, 1f, 0f, 1f,
                color.getRGB(),
                context.scissorStack.peekLast()
        ));

        matrices.popMatrix();
    }


    public int getWidth() {
        return cachedTexture.rectangle().width / 2;
    }

    public int getHeight() {
        return cachedTexture.rectangle().height / 2;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
