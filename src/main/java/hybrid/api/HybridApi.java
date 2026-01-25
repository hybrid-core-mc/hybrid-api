package hybrid.api;

import hybrid.api.screen.HybridScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HybridApi implements ModInitializer {

    public static final String MOD_ID = "hybrid-api";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final KeyBinding TOGGLE_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.hybrid.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, KeyBinding.Category.DEBUG));

    @Override
    public void onInitialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (TOGGLE_KEY.wasPressed()) mc.setScreen(new HybridScreen("lol",500,300));
        });

        LOGGER.info("Hello MONKEY world!");
    }
}