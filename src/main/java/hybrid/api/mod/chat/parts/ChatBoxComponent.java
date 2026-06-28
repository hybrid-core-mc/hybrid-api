package hybrid.api.mod.chat.parts;

import com.mojang.brigadier.suggestion.Suggestion;
import hybrid.api.Main;
import hybrid.api.mod.chat.parts.commands.CommandTreeHelper;
import hybrid.api.theme.ThemeManager;
import hybrid.api.theme.ThemeTarget;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChatBoxComponent {

    private final CommandTreeHelper helper;
    private final List<Float> hoverOffsets = new ArrayList<>();
    ChatTypingComponent chatTypingComponent;
    private List<Suggestion> activeSuggestions = new ArrayList<>();
    private String lastCheckedQuery = "";
    private float animationProgress = 0f;
    private long lastFrameTime = System.currentTimeMillis();
    private float currentScrollY = 0f;
    private float targetScrollY = 0f;
    private Quad lastRenderedCommandBox = new Quad(0, 0, 0, 0);

    public ChatBoxComponent(ChatTypingComponent chatTypingComponent) {
        helper = new CommandTreeHelper("");
        this.chatTypingComponent = chatTypingComponent;
    }

    public void render(Quad quad, String commands, Quad typingBounds, int alpha, boolean isCommandOpen, int mouseX, int mouseY) {

        if (chatTypingComponent.getText().startsWith("/")) isCommandOpen = true;

        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastFrameTime) / 1000f;
        lastFrameTime = currentTime;

        float targetProgress = isCommandOpen ? 1.0f : 0.0f;
        float animationSpeed = 12.0f;
        if (Math.abs(animationProgress - targetProgress) > 0.001f) {
            animationProgress += (targetProgress - animationProgress) * animationSpeed * deltaTime;
            animationProgress = Math.max(0f, Math.min(1f, animationProgress));
        } else {
            animationProgress = targetProgress;
        }

        float scrollSpeed = 14.0f;
        if (Math.abs(currentScrollY - targetScrollY) > 0.01f) {
            currentScrollY += (targetScrollY - currentScrollY) * scrollSpeed * deltaTime;
        } else {
            currentScrollY = targetScrollY;
        }

        int headingSpacing = 25;

        Color outlineColor;
        if (alpha >= 200) {
            outlineColor = new Color(ThemeManager.get(ThemeTarget.ACCENT).getRGB(), true);
        } else {
            outlineColor = Color.GRAY;
        }

        HybridRenderer2D.drawRoundRect(
                quad.copy().subtractY(headingSpacing).addHeight(headingSpacing),
                new Color(12, 15, 23, alpha),
                outlineColor,
                8,
                0.4f
        );
        Color base = new Color(24, 26, 36, alpha);

        float factor = 0.55f;
        int r = base.getRed() + (int) ((255 - base.getRed()) * factor);
        int g = base.getGreen() + (int) ((255 - base.getGreen()) * factor);
        int b = base.getBlue() + (int) ((255 - base.getBlue()) * factor);
        Color lighter = new Color(r, g, b, base.getAlpha());

        Quad line = quad.copy().addHeight(headingSpacing).setHeight(1).addX(5).subtractWidth(10);

        RenderContext.get().enableScissor(line.x, line.y, line.width + line.x, line.y + 1);
        HybridRenderer2D.drawRoundRect(line, new Color(0, 0, 0, 0), lighter, 0, 0.8f, 0, 0, 0, 0);
        RenderContext.get().disableScissor();

        Color base1 = new Color(ThemeManager.get(ThemeTarget.ACCENT).getRGB(), true);
        Color withAlpha = new Color(base1.getRed(), base1.getGreen(), base1.getBlue(), alpha);

        HybridRenderer2D.drawCircle(line.copy().subtractY(15).addX(5), 3f, withAlpha, alpha > 50);
        HybridRenderText text = HybridTextRenderer.getTextRenderer("HYBRID CHAT", FontStyle.EXTRABOLD, 16, new Color(255, 255, 255, alpha), false);
        text.setPosition(line.x + 18, line.y - text.getHeight() - 8);
        HybridTextRenderer.addText(text);

        
        String searchInput = commands.startsWith("/") ? commands.substring(1) : commands;
        if (!searchInput.equals(lastCheckedQuery)) {
            lastCheckedQuery = searchInput;
            helper.setInput(searchInput);

            targetScrollY = 0f;
            currentScrollY = 0f;
            helper.update().thenAccept(suggestions -> {
                if (suggestions != null) {
                    this.activeSuggestions = suggestions;

                    this.hoverOffsets.clear();
                    for (int i = 0; i < suggestions.size(); i++) {
                        this.hoverOffsets.add(6f);
                    }
                }
            });
        }

        if (animationProgress > 0.01f) {
            float padding = 8;
            float fontHeight = 10;
            float itemSpacing = 10;
            float rowLineHeight = fontHeight + itemSpacing;

            int maxVisibleEntries = 5;
            int displayCount = Math.max(1, Math.min(maxVisibleEntries, activeSuggestions.size()));
            float menuHeight = (displayCount * rowLineHeight) + (padding * 2) - 5;

            float startXOffset = 6f;
            float maxHoverBonusX = 4f;

            float targetMenuWidth = 140f;
            if (!activeSuggestions.isEmpty()) {
                float longestStringWidth = 0f;
                for (Suggestion suggestion : activeSuggestions) {
                    
                    String fullString = suggestion.getText();
                    float currentStrWidth = Main.getStyle().getWidth(fullString, (int) fontHeight);
                    if (currentStrWidth > longestStringWidth) {
                        longestStringWidth = currentStrWidth;
                    }
                }
                targetMenuWidth = Math.max(targetMenuWidth, longestStringWidth + (padding * 2) + 12 + startXOffset + maxHoverBonusX);
            }

            int calculatedAlpha = (int) (alpha * animationProgress);
            float maxSlideOffsetX = 12f;
            float slideXOffset = (1f - animationProgress) * -maxSlideOffsetX;

            Quad commandBoxQuad = typingBounds.copy()
                                              .addX((int) (typingBounds.getWidth() + 16 + slideXOffset))
                                              .setWidth((int) targetMenuWidth)
                                              .setHeight((int) menuHeight)
                                              .setY((int) (typingBounds.getY() - (menuHeight - typingBounds.getHeight())))
                    ;

            this.lastRenderedCommandBox = commandBoxQuad;

            HybridRenderer2D.drawRoundRect(
                    commandBoxQuad,
                    new Color(11, 14, 24, calculatedAlpha),
                    new Color(93, 91, 246, calculatedAlpha),
                    6,
                    0.4f
            );

            float renderX = commandBoxQuad.x + padding;
            float renderY = commandBoxQuad.y + padding;

            if (activeSuggestions.isEmpty()) {
                Main.RENDERER.drawText(
                        Main.getStyle(),
                        "No commands found.",
                        renderX + startXOffset,
                        renderY,
                        fontHeight,
                        new Color(255, 255, 255, (int) (calculatedAlpha * 0.4f)).getRGB(),
                        1,
                        commandBoxQuad
                );
            } else {
                float maxScrollY = Math.max(0, (activeSuggestions.size() - maxVisibleEntries) * rowLineHeight);
                if (targetScrollY > maxScrollY) targetScrollY = maxScrollY;

                while (hoverOffsets.size() < activeSuggestions.size()) hoverOffsets.add(startXOffset);

                RenderContext.get().enableScissor(commandBoxQuad.x, commandBoxQuad.y + (int) padding, commandBoxQuad.x + commandBoxQuad.width, commandBoxQuad.y + commandBoxQuad.height - (int) padding);

                for (int i = 0; i < activeSuggestions.size(); i++) {
                    String suggestionText = activeSuggestions.get(i).getText();
                    float currentItemY = renderY + (i * rowLineHeight) - currentScrollY;

                    if (currentItemY + fontHeight >= commandBoxQuad.y && currentItemY <= commandBoxQuad.y + menuHeight) {

                        float rowX = commandBoxQuad.x + 4;
                        float rowY = currentItemY - (itemSpacing / 2f) + 1;
                        float rowWidth = commandBoxQuad.width - 8;

                        boolean isHovered = mouseX >= rowX && mouseX <= rowX + rowWidth &&
                                mouseY >= rowY && mouseY <= rowY + rowLineHeight;

                        float currentXShift = hoverOffsets.get(i);
                        float targetXShift = isHovered ? (startXOffset + maxHoverBonusX) : startXOffset;

                        if (Math.abs(currentXShift - targetXShift) > 0.01f) {
                            currentXShift += (targetXShift - currentXShift) * 16.0f * deltaTime;
                            hoverOffsets.set(i, currentXShift);
                        } else {
                            hoverOffsets.set(i, targetXShift);
                        }

                        if (isHovered) {
                            Quad hoverQuad = new Quad((int) rowX, (int) rowY, (int) rowWidth, (int) rowLineHeight);
                            HybridRenderer2D.drawRoundRect(
                                    hoverQuad,
                                    new Color(24, 26, 58, calculatedAlpha),
                                    new Color(0, 0, 0, 0),
                                    4,
                                    0f
                            );
                        }

                        Main.RENDERER.drawText(
                                Main.getStyle(),
                                suggestionText,
                                renderX + currentXShift,
                                currentItemY,
                                fontHeight,
                                new Color(140, 138, 255, calculatedAlpha).getRGB(),
                                1,
                                commandBoxQuad
                        );
                    }
                }

                RenderContext.get().disableScissor();
            }
        }
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (animationProgress > 0.1f && !activeSuggestions.isEmpty()) {
            if (mouseX >= lastRenderedCommandBox.x && mouseX <= lastRenderedCommandBox.x + lastRenderedCommandBox.width &&
                    mouseY >= lastRenderedCommandBox.y && mouseY <= lastRenderedCommandBox.y + lastRenderedCommandBox.height) {

                float fontHeight = 10;
                float itemSpacing = 10;
                float rowLineHeight = fontHeight + itemSpacing;
                int maxVisibleEntries = 5;

                if (verticalAmount < 0) {
                    targetScrollY += rowLineHeight;
                } else if (verticalAmount > 0) {
                    targetScrollY -= rowLineHeight;
                }

                float maxScrollY = Math.max(0, (activeSuggestions.size() - maxVisibleEntries) * rowLineHeight);

                if (targetScrollY < 0f) targetScrollY = 0f;
                if (targetScrollY > maxScrollY) targetScrollY = maxScrollY;
            }
        }
    }
    public void mouseReleased(MouseButtonEvent mouseButtonEvent) {
        double mouseX = mouseButtonEvent.x();
        double mouseY = mouseButtonEvent.y();
        int button = mouseButtonEvent.button();

        if (button == 0 && animationProgress > 0.1f && !activeSuggestions.isEmpty()) {
            if (mouseX >= lastRenderedCommandBox.x && mouseX <= lastRenderedCommandBox.x + lastRenderedCommandBox.width &&
                    mouseY >= lastRenderedCommandBox.y && mouseY <= lastRenderedCommandBox.y + lastRenderedCommandBox.height) {

                float padding = 8;
                float fontHeight = 10;
                float itemSpacing = 10;
                float rowLineHeight = fontHeight + itemSpacing;

                float renderY = lastRenderedCommandBox.y + padding;

                for (int i = 0; i < activeSuggestions.size(); i++) {
                    float currentItemY = renderY + (i * rowLineHeight) - currentScrollY;
                    float rowY = currentItemY - (itemSpacing / 2f) + 1;

                    if (mouseY >= rowY && mouseY <= rowY + rowLineHeight) {
                        Suggestion clickedSuggestion = activeSuggestions.get(i);
                        String suggestionText = clickedSuggestion.getText();

                        String currentTyped = chatTypingComponent.getText();

                        if (currentTyped.equals("/") && suggestionText.startsWith("/")) {
                            
                            suggestionText = suggestionText.substring(1);
                        } else if (currentTyped.isEmpty()) {
                            
                            if (!suggestionText.startsWith("/")) {
                                suggestionText = "/" + suggestionText;
                            }
                        }

                        String toAppend = suggestionText + " ";
                        chatTypingComponent.appendToCurrentText(toAppend);
                        break;
                    }
                }
            }
        }

    }
}