package hybrid.api.font;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public record HybridFontTexture(NativeImageBackedTexture texture, Rectangle rectangle, String text) {
    public static HybridFontTexture createGlyph(HybridRenderText hybridRenderText, String text, Color shadowColor, boolean shadow, int shadowRadius) {

        Font font = hybridRenderText.getFont();

        boolean fontMode = text != null;
        boolean svgMode = text == null;

        if (fontMode && font == null) {
            throw new RuntimeException("No font provided"); // todo: wtf bro use real logging errors
        }

        if (svgMode && hybridRenderText.getSvgDocument() == null) {
            throw new RuntimeException("No SVG provided");
        }

        Rectangle rectangle;

        if (fontMode) {
            FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
            GlyphVector gv = font.createGlyphVector(frc, text);
            rectangle = gv.getPixelBounds(null, 0, 0);
        } else {
            var size = hybridRenderText.getSvgDocument().size();
            rectangle = new Rectangle(0, 0, Math.max(1, (int) Math.ceil(size.width)), Math.max(1, (int) Math.ceil(size.height)));
        }

        int shadowPad = shadow ? 2 : 0;

        int width = Math.max(1, rectangle.width + shadowPad);
        int height = Math.max(1, rectangle.height + shadowPad);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();

        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(AlphaComposite.SrcOver);

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int drawX = fontMode ? -rectangle.x : 0;
        int drawY = fontMode ? -rectangle.y : 0;

        if (shadow && fontMode) {
            graphics.setFont(font);
            graphics.setPaint(shadowColor);
            graphics.drawString(text, drawX + 1, drawY + 1);
            if (shadowRadius > 1) graphics.drawString(text, drawX + 2, drawY + 2); // todo: stop being lazy
        }

        if (fontMode) {
            graphics.setFont(font);
            graphics.setPaint(Color.WHITE);
            graphics.drawString(text, drawX, drawY);
        } else {
            hybridRenderText.getSvgDocument().render(null, graphics);
        }

        graphics.dispose();

        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < pixels.length; i++) {
            int x = i % width;
            int y = i / width;
            nativeImage.setColorArgb(x, y, pixels[i]);
        }

        NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> String.format("%s Custom Font", text), nativeImage);
        texture.upload();

        return new HybridFontTexture(texture, rectangle, text);
    }


}
