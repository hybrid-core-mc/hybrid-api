package hybrid.api.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import hybrid.api.shader.HueShader;
import net.minecraft.client.gui.render.GuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {

    @WrapOperation(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;" + "Lcom/mojang/blaze3d/systems/RenderPass;" + "Lcom/mojang/blaze3d/buffers/GpuBuffer;" + "Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"))
    private void bindHueUniform(RenderPass pass, int baseVertex, int zero, int indexCount, int one, Operation<Void> original) {
        GpuBufferSlice slice = HueShader.CURRENT_UNIFORM.get();
        if (slice != null) {
            pass.setUniform("Uniforms", slice);
        }

        original.call(pass, baseVertex, zero, indexCount, one);
    }

    @Inject(method = "render*", at = @At("RETURN"))
    private void endFrame(GpuBufferSlice bufferSlice, CallbackInfo ci) {
        HueShader.clearUniforms();
    }
}