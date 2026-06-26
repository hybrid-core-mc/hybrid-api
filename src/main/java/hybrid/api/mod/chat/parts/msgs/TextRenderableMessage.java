package hybrid.api.mod.chat.parts.msgs;

import hybrid.api.Main;
import hybrid.api.mod.chat.parts.ChatTextComponent;
import hybrid.api.mod.chat.parts.ChatLayoutController;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.texture.HybridTextureRenderer;
import net.minecraft.network.chat.Style;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextRenderableMessage extends RenderableMessage {

    private record TextRun(String text, int color) {}
    private final List<List<TextRun>> wrappedLines = new ArrayList<>();
    private final float lineHeight;

    public TextRenderableMessage(ChatTextComponent.ChatMessage msg, boolean isGrouped, Quad clipping, boolean playerHeads) {
        super(msg, isGrouped, clipping, playerHeads);

        float fontSize = ChatLayoutController.getChatFontSize();
        this.lineHeight = fontSize;

        float maxWidth = clipping.width;

        wrapText(msg.message(), fontSize, maxWidth);
    }
    private void wrapText(net.minecraft.network.chat.Component textComponent, float fontSize, float maxWidth) {
        List<TextRun> currentLine = new ArrayList<>();
        final float[] currentLineWidth = {0f};

        textComponent.visit((style, literalText) -> {
            if (literalText.isEmpty()) return java.util.Optional.empty();

            int color = style.getColor() != null ? style.getColor().getValue() | 0xFF000000 : 0xFFD0D0D0;
            String[] words = literalText.split("(?<=\\s)"); 

            for (String word : words) {
                float wordWidth = Main.getStyle().getWidth(word, fontSize);

                
                if (currentLineWidth[0] + wordWidth <= maxWidth) {
                    currentLine.add(new TextRun(word, color));
                    currentLineWidth[0] += wordWidth;
                }
                
                else if (wordWidth > maxWidth) {
                    
                    if (!currentLine.isEmpty()) {
                        wrappedLines.add(new ArrayList<>(currentLine));
                        currentLine.clear();
                        currentLineWidth[0] = 0f;
                    }

                    StringBuilder currentChunk = new StringBuilder();
                    for (int i = 0; i < word.length(); i++) {
                        char c = word.charAt(i);
                        String charStr = String.valueOf(c);
                        float charWidth = Main.getStyle().getWidth(charStr, fontSize);

                        if (currentLineWidth[0] + charWidth <= maxWidth) {
                            currentChunk.append(c);
                            currentLineWidth[0] += charWidth;
                        } else {
                            if (!currentChunk.isEmpty()) {
                                currentLine.add(new TextRun(currentChunk.toString(), color));
                                wrappedLines.add(new ArrayList<>(currentLine));
                                currentLine.clear();
                                currentChunk.setLength(0);
                            }
                            
                            currentChunk.append(c);
                            currentLineWidth[0] = charWidth;
                        }
                    }
                    
                    if (!currentChunk.isEmpty()) {
                        currentLine.add(new TextRun(currentChunk.toString(), color));
                    }
                }
                
                else {
                    if (!currentLine.isEmpty()) {
                        wrappedLines.add(new ArrayList<>(currentLine));
                        currentLine.clear();
                    }
                    currentLine.add(new TextRun(word, color));
                    currentLineWidth[0] = wordWidth;
                }
            }
            return java.util.Optional.empty();
        }, Style.EMPTY);

        
        if (!currentLine.isEmpty()) {
            wrappedLines.add(currentLine);
        }
    }

    @Override
    public float getHeight() {
        int lineCount = Math.max(1, wrappedLines.size());
        float textHeight = lineCount * lineHeight;

        
        if (!isGrouped) {
            float usernameHeaderHeight = ChatLayoutController.getUsernameFontSize() + ChatLayoutController.usernameToTextSpacing;
            return usernameHeaderHeight + textHeight;
        }

        return textHeight;
    }

    @Override
    protected void renderContent(HybridTextureRenderer textureRenderer, float contentX, float bodyY, Quad textClipping) {
        float fontSize = ChatLayoutController.getChatFontSize();
        float currentY = bodyY;

        for (List<TextRun> line : wrappedLines) {
            float currentX = contentX;

            for (TextRun run : line) {
                Main.RENDERER.drawText(
                        Main.getStyle(),
                        run.text(),
                        currentX,
                        currentY,
                        fontSize,
                        run.color(),1,
                        textClipping
                );
                currentX += Main.getStyle().getWidth(run.text(), fontSize);
            }
            currentY += lineHeight; 
        }
    }
}