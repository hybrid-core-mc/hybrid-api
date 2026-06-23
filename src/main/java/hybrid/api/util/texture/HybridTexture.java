package hybrid.api.util.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class HybridTexture implements ResourceManagerReloadListener {
    private static final List<HybridTexture> ALL_TEXTURES = new ArrayList<>();

    private final Identifier location;
    private final NativeImage providedImage; 
    public GpuTexture texture;
    public GpuTextureView view;
    public int width;
    public int height;

    
    public HybridTexture(Identifier location) {
        this.location = location;
        this.providedImage = null;
        this.load();
        ALL_TEXTURES.add(this);
    }

    
    public HybridTexture(Identifier location, NativeImage dynamicImage) {
        this.location = location;
        this.providedImage = dynamicImage;
        this.width = dynamicImage.getWidth();
        this.height = dynamicImage.getHeight();
        this.loadDynamic();
        ALL_TEXTURES.add(this);
    }

    private void loadDynamic() {
        if (view != null) { view.close(); view = null; }
        if (texture != null) { texture.close(); texture = null; }

        
        texture = RenderSystem.getDevice().createTexture(
                location.toString(),
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST,
                TextureFormat.RGBA8,
                width, height, 1, 1
        );

        
        int bufferSize = width * height * 4;

        
        java.nio.ByteBuffer buffer = org.lwjgl.system.MemoryUtil.memByteBuffer(providedImage.getPointer(), bufferSize);

        
        RenderSystem.getDevice().createCommandEncoder()
                    .writeToTexture(texture, buffer, NativeImage.Format.RGBA, 0, 0, 0, 0, width, height);

        view = RenderSystem.getDevice().createTextureView(texture);
    }

    private void load() {
        
        if (providedImage != null) {
            loadDynamic();
            return;
        }

        if (view != null) { view.close(); view = null; }
        if (texture != null) { texture.close(); texture = null; }

        ByteBuffer pixels;
        ByteBuffer rawBuffer = null;

        try {
            InputStream stream = Minecraft.getInstance().getResourceManager().open(location);
            byte[] bytes = stream.readAllBytes();
            stream.close();

            rawBuffer = MemoryUtil.memAlloc(bytes.length);
            rawBuffer.put(bytes).flip();

            int[] widthArr = new int[1];
            int[] heightArr = new int[1];
            int[] channelsArr = new int[1];

            pixels = STBImage.stbi_load_from_memory(rawBuffer, widthArr, heightArr, channelsArr, 4);
            if (pixels == null) {
                throw new RuntimeException("STBImage failure: " + STBImage.stbi_failure_reason());
            }

            width = widthArr[0];
            height = heightArr[0];

        } catch (Exception e) {
            System.out.println("Failed to load texture '" + location + "': " + e.getMessage() + " — using missing texture");
            pixels = makeMissingTexture();
            width = 16;
            height = 16;
        } finally {
            if (rawBuffer != null) {
                MemoryUtil.memFree(rawBuffer);
            }
        }

        texture = RenderSystem.getDevice().createTexture(
                location.toString(),
                GpuTexture.USAGE_TEXTURE_BINDING | GpuTexture.USAGE_COPY_DST,
                TextureFormat.RGBA8,
                width, height, 1, 1
        );

        RenderSystem.getDevice().createCommandEncoder()
                    .writeToTexture(texture, pixels, NativeImage.Format.RGBA, 0, 0, 0, 0, width, height);

        if (width == 16 && height == 16 && pixels.remaining() == 16 * 16 * 4) {
            MemoryUtil.memFree(pixels);
        } else {
            STBImage.stbi_image_free(pixels);
        }

        view = RenderSystem.getDevice().createTextureView(texture);
    }

    private static ByteBuffer makeMissingTexture() {
        ByteBuffer buf = MemoryUtil.memAlloc(16 * 16 * 4);
        for (int py = 0; py < 16; py++) {
            for (int px = 0; px < 16; px++) {
                boolean magenta = (px < 8) != (py < 8);
                buf.put((byte) (magenta ? 0xFF : 0x00));
                buf.put((byte) 0x00);
                buf.put((byte) (magenta ? 0xFF : 0x00));
                buf.put((byte) 0xFF);
            }
        }
        buf.flip();
        return buf;
    }

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        this.load();
    }

    public void close() {
        ALL_TEXTURES.remove(this);
        if (view != null) view.close();
        if (texture != null) texture.close();
        if (providedImage != null) providedImage.close(); 
    }
}