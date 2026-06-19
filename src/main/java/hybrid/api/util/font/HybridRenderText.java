package hybrid.api.util.font;

import com.github.weisj.jsvg.SVGDocument;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.BlitRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class HybridRenderText {

    public HybridFontTexture cachedTexture;
    String text;
    int x, y, shadowRadius;
    Color color, shadowColor;
    Font font;
    SVGDocument svgDocument;
    boolean shadow;

    public HybridRenderText(String text, int x, int y, Font font, Color shadowColor, Color color, boolean shadow, int shadowRadius) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.font = font;
        this.shadowColor = shadowColor;
        this.color = color;
        this.shadow = shadow;
        this.shadowRadius = shadowRadius;

    }

    public HybridRenderText(int x, int y, SVGDocument svgDocument, Color color) {
        this.x = x;
        this.y = y;
        this.svgDocument = svgDocument;
        this.color = color;

    }

    public SVGDocument getSvgDocument() {
        return svgDocument;
    }

    public Font getFont() {
        return font;
    }


    private void ensureTextureInitialized() {
        if (this.cachedTexture != null) return;

        if (svgDocument == null && text != null) {

            String fontName = (font != null) ? font.getName() : "default";
            int fontSize = (font != null) ? font.getSize() : 0;
            int fontStyle = (font != null) ? font.getStyle() : 0;
            String sColorStr = (shadowColor != null) ? String.valueOf(shadowColor.getRGB()) : "null";

            String key = text + "|" + fontName + "|" + fontSize + "|" + fontStyle + "|" + color.getRGB() + "|" + shadow + "|" + sColorStr + "|" + shadowRadius;

            HybridRenderText globallyCached = HybridTextRenderer.textCache.get(key);

            if (globallyCached != null && globallyCached.cachedTexture != null) {
                this.cachedTexture = globallyCached.cachedTexture;
            } else {
                this.cachedTexture = HybridFontTexture.createGlyph(this, text, shadowColor, shadow, shadowRadius);
                HybridTextRenderer.textCache.put(key, this);
            }
        } else {

            String svgKey = "svg_" + (svgDocument != null ? System.identityHashCode(svgDocument) : "null") + "|" + color.getRGB();

            HybridRenderText globallyCachedIcon = HybridTextRenderer.textCache.get(svgKey);

            if (globallyCachedIcon != null && globallyCachedIcon.cachedTexture != null) {
                this.cachedTexture = globallyCachedIcon.cachedTexture;
            } else {
                this.cachedTexture = HybridFontTexture.createGlyph(this, null, null, false, 0);
                HybridTextRenderer.textCache.put(svgKey, this);
            }
        }
    }

    public void draw(GuiGraphics context) {
        boolean isVanilla = false;

        if (isVanilla && svgDocument == null) {

        } else {
            ensureTextureInitialized();

            if (this.cachedTexture == null || this.cachedTexture.texture() == null) {
                return;
            }

            Matrix3x2fStack matrices = context.pose();
            matrices.pushMatrix();

            matrices.translate(x, y);
            matrices.scale(0.5f, 0.5f);

            context.guiRenderState.submitGuiElement(new BlitRenderState(
                    RenderPipelines.GUI_TEXTURED,
                    TextureSetup.singleTexture(cachedTexture.texture().getTextureView(), cachedTexture.texture().getSampler()),
                    new Matrix3x2f(matrices),
                    0, 0,
                    cachedTexture.rectangle().width, cachedTexture.rectangle().height,
                    0f, 1f, 0f, 1f,
                    color.getRGB(),
                    context.scissorStack.peek()
            ));

            matrices.popMatrix();
        }
    }

    public int getWidth() {
        ensureTextureInitialized();
        return (cachedTexture != null) ? cachedTexture.rectangle().width / 2 : 0;
    }

    public int getHeight() {
        ensureTextureInitialized();
        return (cachedTexture != null) ? cachedTexture.rectangle().height / 2 : 0;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setColor(Color color) {
        this.color = color;
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

    public int getCharWidth(char c) {
        return 0;
    }
}