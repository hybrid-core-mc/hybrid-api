package hybrid.api.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import hybrid.api.shader.HueShader;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiRenderer.class)
public abstract class GuiRendererMixin {


    @WrapOperation(method = "render(Lnet/minecraft/client/gui/render/GuiRenderer$Draw;Lcom/mojang/blaze3d/systems/RenderPass;Lcom/mojang/blaze3d/buffers/GpuBuffer;Lcom/mojang/blaze3d/vertex/VertexFormat$IndexType;)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"))
    private void drawIndexed(RenderPass instance, int baseVertex, int zero, int indexCount, int one, Operation<Void> original, @Local(argsOnly = true) GuiRenderer.Draw draw) {

        HueShader toggleRenderState = HueShader.RESTORE.remove(System.identityHashCode(draw.textureSetup()));
        if (toggleRenderState != null && toggleRenderState.uniformBuffer != null) {
            instance.setUniform("Uniforms", toggleRenderState.uniformBuffer);
        }

        original.call(instance, baseVertex, zero, indexCount, one);
    }

    @WrapOperation(method = "prepareSimpleElement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;scissorChanged(Lnet/minecraft/client/gui/ScreenRect;Lnet/minecraft/client/gui/ScreenRect;)Z"))
    private boolean prepareSimpleElement(GuiRenderer instance, ScreenRect oldScissorArea, ScreenRect newScissorArea, Operation<Boolean> original, SimpleGuiElementRenderState renderState) {
        if (renderState instanceof HueShader) {
            return true;
        }
        return original.call(instance, oldScissorArea, newScissorArea);
    }


    @Inject(method = "render*", at = @At("RETURN"))
    private void render(GpuBufferSlice bufferSlice, CallbackInfo ci) {

        HueShader.RESTORE.clear();
        HueShader.Uniforms.instance.clear();

    }

}