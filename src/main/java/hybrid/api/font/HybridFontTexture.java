package hybrid.api.font;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;


public record HybridFontTexture(NativeImageBackedTexture texture, Rectangle rectangle, String text) {

    public static HybridFontTexture createGlyph(HybridRenderText hybridRenderText, String text) {

        Font font = hybridRenderText.getFont();

        if (font == null) throw new RuntimeException("Unable to create glyph texture invalid font provided");

        FontRenderContext fontRenderContext = new FontRenderContext(new AffineTransform(), true, true);
        GlyphVector glyphVector = font.createGlyphVector(fontRenderContext, text);
        Rectangle rectangle = glyphVector.getPixelBounds(null, 0, 0);

        int width = Math.max(1, rectangle.width);
        int height = Math.max(1, rectangle.height);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = bufferedImage.createGraphics();

        graphics.setFont(font);
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setPaint(Color.WHITE);

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        graphics.drawString(text, -rectangle.x, -rectangle.y);
        graphics.dispose();

        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);

        int[] pixels = bufferedImage.getRGB(0, 0, width, height, null, 0, width); // copy the awt texture into a mc texture LOL

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
