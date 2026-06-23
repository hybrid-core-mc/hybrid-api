package hybrid.api.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import hybrid.api.util.chat.CustomChatScreen;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.nio.file.Path;
import java.nio.file.Paths;

import static hybrid.api.Main.mc;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;setupMouseCallbacks(Lcom/mojang/blaze3d/platform/Window;Lorg/lwjgl/glfw/GLFWCursorPosCallbackI;Lorg/lwjgl/glfw/GLFWMouseButtonCallbackI;Lorg/lwjgl/glfw/GLFWScrollCallbackI;Lorg/lwjgl/glfw/GLFWDropCallbackI;)V"))
    private void wrapCallbacks(Window window, GLFWCursorPosCallbackI moveCallback, GLFWMouseButtonCallbackI buttonCallback, GLFWScrollCallbackI scrollCallback, GLFWDropCallbackI dropCallback) {

        GLFWDropCallbackI wrappedDrop = (handle, count, names) -> {

            dropCallback.invoke(handle, count, names);

            for (int i = 0; i < count; i++) {
                String path = GLFWDropCallback.getName(names, i);
                Path p = Paths.get(path);

                if (p.toString().toLowerCase().endsWith(".gif")) {
                    if (mc.screen instanceof CustomChatScreen screen) {
                        screen.submitGif(String.valueOf(p));
                    }
                }

            }
        };

        InputConstants.setupMouseCallbacks(window, moveCallback, buttonCallback, scrollCallback, wrappedDrop);
    }
}