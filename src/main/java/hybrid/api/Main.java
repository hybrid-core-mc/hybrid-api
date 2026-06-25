package hybrid.api;

import com.mojang.blaze3d.platform.InputConstants;
import hybrid.api.mod.HybridMods;
import hybrid.api.theme.ThemeManager;
import hybrid.api.mod.chat.CustomChatScreen;
import hybrid.api.ui.HybridGuiScreen;
import hybrid.api.util.font.fancy.FontRenderer;
import hybrid.api.util.font.fancy.StyledFont;
import hybrid.api.util.shader.HybridShaders;
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
	public static final FontRenderer RENDERER = new FontRenderer();

	public static StyledFont INTER_BOLD;

	public static StyledFont getStyle() {
		if (INTER_BOLD == null) {
			INTER_BOLD = new StyledFont(Identifier.fromNamespaceAndPath("hybrid-api", "font/inter-bold.ttf"));
		}
		return INTER_BOLD;
	}


	@Override
	public void onInitialize() {


		LOGGER.info("Hello HELL!!");

		HybridShaders.init();
		HybridMods.init();
		ThemeManager.init();

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

		KeyMapping chat = KeyBindingHelper.registerKeyBinding(
				new KeyMapping(
						Component.translatable("test").getString(),
						InputConstants.Type.KEYSYM,
						GLFW.GLFW_KEY_Z,
						CATEGORY
				));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (sendToChatKey.consumeClick()) {
				mc.setScreen(new HybridGuiScreen());
			}
			while (chat.consumeClick()) {
				mc.setScreen(new CustomChatScreen());
			}
		});

    }
}