package hybrid.api;

import com.mojang.blaze3d.platform.InputConstants;
import dev.bsprout.brapi.client.BRender;
import hybrid.api.ui.HybridGuiScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    public static final String MOD_ID = "hybrid-api";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Minecraft mc = Minecraft.getInstance();

    @Override
    public void onInitialize() {


        LOGGER.info("Hello HELL!!");

		KeyMapping.Category CATEGORY = KeyMapping.Category.register(
				Identifier.fromNamespaceAndPath(MOD_ID, "hybrid-api")
		);


		KeyMapping sendToChatKey = KeyBindingHelper.registerKeyBinding(
				new KeyMapping(
						Component.translatable("hybrid.keybinding").getString(),
						InputConstants.Type.KEYSYM,
						GLFW.GLFW_KEY_J,
						CATEGORY
				));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (sendToChatKey.consumeClick()) {
				mc.setScreen(new HybridGuiScreen());
			}
		});

    }
}