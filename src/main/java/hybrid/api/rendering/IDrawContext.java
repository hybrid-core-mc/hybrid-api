package hybrid.api.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.gl.GpuSampler;

public interface IDrawContext {
    void hybrid_api$drawTexturedQuad(RenderPipeline pipeline, GpuTextureView texture, GpuSampler sampler, int x1, int y1, int x2, int y2, float u1, float v1, float u2, float v2, int color);
}
