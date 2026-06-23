package hybrid.api.util.texture;

import net.minecraft.resources.Identifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextureCache {

    private static final Map<Identifier, HybridTexture> CACHE = new ConcurrentHashMap<>();

    public static HybridTexture getOrCreate(Identifier location) {
        if (location == null) {
            return null;
        }
        return CACHE.computeIfAbsent(location, HybridTexture::new);
    }

    
    public static void putDirectly(Identifier location, HybridTexture texture) {
        if (location != null && texture != null) {
            CACHE.put(location, texture);
        }
    }

    public static void clear() {
        CACHE.values().forEach(HybridTexture::close);
        CACHE.clear();
    }
}