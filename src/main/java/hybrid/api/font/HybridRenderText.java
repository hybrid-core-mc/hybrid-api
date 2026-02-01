package hybrid.api.font;

import com.github.weisj.jsvg.SVGDocument;
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
    int x, y,shadowRadius;
    Color color, shadowColor;
    Font font;
    SVGDocument svgDocument;
    boolean shadow;

    private HybridFontTexture cachedTexture;

    public HybridRenderText(String text, int x, int y, Font font, Color shadowColor, Color color, boolean shadow, int shadowRadius) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.color = color;
        this.shadow = shadow;
        this.shadowRadius = shadowRadius;
        this.cachedTexture = HybridFontTexture.createGlyph(this, text, shadowColor, shadow,shadowRadius);
    }

    public HybridRenderText(int x, int y, SVGDocument svgDocument, Color color) {
        this.x = x;
        this.y = y;
        this.svgDocument = svgDocument;
        this.color = color;
        this.cachedTexture = HybridFontTexture.createGlyph(this, null, null, false, 0);
    }


    public SVGDocument getSvgDocument() {
        return svgDocument;
    }

    public Font getFont() {
        return font;
    }

    public void draw(DrawContext context) {

        if (cachedTexture == null || (text != null && !text.equals(cachedTexture.text()))) {
            cachedTexture = HybridFontTexture.createGlyph(this, text, shadowColor, shadow, shadowRadius);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
