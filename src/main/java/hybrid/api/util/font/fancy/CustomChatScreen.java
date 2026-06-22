package hybrid.api.util.font.fancy;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class CustomChatScreen extends Screen {
    private final FontRenderer magicFont = new FontRenderer();
    private StyledFont font;
    private String text = "Type here...";

    public CustomChatScreen() {
        super(Component.literal("BRender Test Screen"));
    }

    @Override
    protected void init() {
        font = new StyledFont(Identifier.fromNamespaceAndPath("hybrid-api", "font/inter-regular.ttf"));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int x = 5;
        int y = 14;
        int size = 32;

        magicFont.drawText(font, text, x, y, size, 0xFFFF8800);
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {

        if (text.equals("Type here...")) {
            text = "";
        }

        text += characterEvent.codepointAsString();
        return super.charTyped(characterEvent);
    }



    @Override
    public boolean keyPressed(KeyEvent keyEvent) {

        if (keyEvent.key() == GLFW.GLFW_KEY_BACKSPACE && !text.isEmpty()) {
            if (text.equals("Type here...")) {
                text = "";
            } else {
                text = text.substring(0, text.length() - 1);
            }
            return true;
        }

        return super.keyPressed(keyEvent);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        if (font != null) font.close();
    }
}