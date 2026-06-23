package hybrid.api.util.chat;

import hybrid.api.Main;
import hybrid.api.util.chat.parts.ChatBoxComponent;
import hybrid.api.util.chat.parts.ChatTextComponent;
import hybrid.api.util.chat.parts.ChatTypingComponent;
import hybrid.api.util.font.fancy.StyledFont;
import hybrid.api.util.render.ExternalImageRenderer;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static hybrid.api.Main.mc;

public class CustomChatScreen extends Screen {
    ChatBoxComponent chatBoxComponent;
    Quad chatBoxBounds;
    ChatTypingComponent chatTypingComponent;
    ChatTextComponent textComponent;

    public CustomChatScreen() {
        super(Component.literal("BRender Test Screen"));
        chatBoxComponent = new ChatBoxComponent();
        chatBoxBounds = new Quad(0, 0, 300, 160);
        textComponent = new ChatTextComponent();
        chatTypingComponent = new ChatTypingComponent(textComponent);
    }

    public void submitGif(String path){
        textComponent.submitGif(mc.player.getPlainTextName(),path);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int margin = 5;


        chatBoxBounds.setX(margin);
        chatBoxBounds.setY(this.height - chatBoxBounds.getHeight() - margin);

        
        chatBoxComponent.render(chatBoxBounds, mouseX, mouseY);

        int padding = 5;
        int inputHeight = 23;

        int inputX = chatBoxBounds.getX() + padding;
        int inputY = chatBoxBounds.getY() + chatBoxBounds.getHeight() - inputHeight - padding;
        int inputWidth = chatBoxBounds.getWidth() - (padding * 2);

        
        Quad typingBounds = new Quad(inputX, inputY, inputWidth, inputHeight);

        
        textComponent.render(typingBounds,chatBoxBounds);


        chatTypingComponent.render(typingBounds);


        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean charTyped(CharacterEvent characterEvent) {
        chatTypingComponent.charTyped(characterEvent);
        return super.charTyped(characterEvent);
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        chatTypingComponent.keyPressed(keyEvent);
        return super.keyPressed(keyEvent);
    }

    @Override
    public void removed() {
        if (Main.INTER_BOLD != null) Main.INTER_BOLD.close();
    }
}