package hybrid.api.util.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static hybrid.api.Main.mc;

public class ExternalImageRenderer {


    
    private static final Map<Path, GifAnimation> CACHE = new HashMap<>();

    
    public static void renderGif(GuiGraphics g, Path path, float x, float y, float w, float h) {
        if (path == null || !Files.exists(path)) return;

        GifAnimation animation = CACHE.get(path);

        
        if (animation == null) {
            animation = loadGif(path);
            if (animation == null) return;
            CACHE.put(path, animation);
        }

        
        Identifier currentFrameId = animation.getCurrentFrameId();

        
        g.blit(RenderPipelines.GUI_TEXTURED,
                currentFrameId,
                (int) x,
                (int) y,
                0,
                0,
                (int) w,
                (int) h,
                (int) w,
                (int) h
        );
    }
    
    private static GifAnimation loadGif(Path path) {
        List<Identifier> frames = new ArrayList<>();
        List<Integer> delays = new ArrayList<>();

        try (InputStream is = Files.newInputStream(path);
             ImageInputStream iis = ImageIO.createImageInputStream(is)) {

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) return null;

            ImageReader reader = readers.next();
            reader.setInput(iis);

            int numImages = reader.getNumImages(true);
            for (int i = 0; i < numImages; i++) {
                BufferedImage bImage = reader.read(i);
                int delay = getFrameDelay(reader, i);

                NativeImage nativeImage = convertToNativeImage(bImage);
                int finalI = i;
                DynamicTexture texture = new DynamicTexture(() -> "frame: " + path.getFileName() + " #" + finalI, nativeImage);
                texture.upload();

                Identifier id = Identifier.fromNamespaceAndPath(
                        "hybrid-api",
                        "gifs/" + UUID.randomUUID()
                );

                mc.getTextureManager().register(id, texture);
                frames.add(id);
                delays.add(delay);
            }

            reader.dispose();

            if (frames.isEmpty()) return null;
            return new GifAnimation(frames, delays);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    
    private static int getFrameDelay(ImageReader reader, int imageIndex) {
        try {
            IIOMetadata metadata = reader.getImageMetadata(imageIndex);
            String metaFormatName = metadata.getNativeMetadataFormatName();
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);

            IIOMetadataNode graphicsControlExtension = getMetadataNode(root, "GraphicControlExtension");
            if (graphicsControlExtension != null) {
                
                int delayTime = Integer.parseInt(graphicsControlExtension.getAttribute("delayTime"));
                return delayTime > 0 ? delayTime * 10 : 100; 
            }
        } catch (Exception ignored) {}
        return 100; 
    }

    private static IIOMetadataNode getMetadataNode(IIOMetadataNode root, String nodeName) {
        int nNodes = root.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (root.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
                return ((IIOMetadataNode) root.item(i));
            }
        }
        return null;
    }

    private static NativeImage convertToNativeImage(BufferedImage bImage) {
        NativeImage nativeImage = new NativeImage(bImage.getWidth(), bImage.getHeight(), false);
        for (int y = 0; y < bImage.getHeight(); y++) {
            for (int x = 0; x < bImage.getWidth(); x++) {
                int rgba = bImage.getRGB(x, y);

                int a = (rgba >> 24) & 0xFF;
                int r = (rgba >> 16) & 0xFF;
                int g = (rgba >> 8) & 0xFF;
                int b = rgba & 0xFF;

                int packed = (a << 24) | (b << 16) | (g << 8) | r;
                nativeImage.setPixelABGR(x, y, packed);
            }
        }
        return nativeImage;
    }

    public static void clearFromCache(Path path) {
        GifAnimation anim = CACHE.remove(path);
        if (anim != null) {
            anim.release();
        }
    }

    public static void clearAllCache() {
        for (GifAnimation anim : CACHE.values()) {
            anim.release();
        }
        CACHE.clear();
    }

    
    private static class GifAnimation {
        private final List<Identifier> frameIds;
        private final List<Integer> delays;
        private final int totalDuration;

        public GifAnimation(List<Identifier> frameIds, List<Integer> delays) {
            this.frameIds = frameIds;
            this.delays = delays;

            int sum = 0;
            for (int d : delays) sum += d;
            this.totalDuration = sum;
        }

        public Identifier getCurrentFrameId() {
            if (frameIds.size() == 1 || totalDuration == 0) return frameIds.getFirst();

            
            long timeInLoop = System.currentTimeMillis() % totalDuration;

            int cumulativeDelay = 0;
            for (int i = 0; i < frameIds.size(); i++) {
                cumulativeDelay += delays.get(i);
                if (timeInLoop < cumulativeDelay) {
                    return frameIds.get(i);
                }
            }
            return frameIds.get(frameIds.size() - 1);
        }

        public void release() {
            for (Identifier id : frameIds) {
                mc.getTextureManager().release(id);
            }
        }
    }
}