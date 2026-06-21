package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.ColorSetting;
import hybrid.api.mod.settings.Setting;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HueCirclePicker;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.TriangleGradientPicker;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ColorComponent extends CategoryComponent {
    private static final Color BASE_FILL = new Color(36, 41, 54, 255);
    private static final Color HOVER_ACTIVE_COLOR = new Color(99, 102, 241, 255);

    private final ColorSetting colorSetting;
    private int height = 36;
    private boolean expanded = false;
    private final Runnable onHeightChanged;
    private Quad quad;
    private final Color[] presets;
    TriangleGradientPicker triangleGradientPicker;
    HueCirclePicker picker;

    private Quad barQuad;
    private boolean isDraggingAlpha = false;
    private float knobColorAnim = 0f;

    private String cachedRgbString = "";
    private HybridRenderText cachedRenderText = null;

    public ColorComponent(Setting<?> setting, Runnable onHeightChanged) {
        super(setting);
        this.colorSetting = (ColorSetting) setting;
        this.onHeightChanged = onHeightChanged;

        this.presets = new Color[6];
        presets[0] = new Color(99, 102, 241, 255);
        presets[1] = new Color(16, 185, 129, 255);
        presets[2] = new Color(244, 63, 94, 255);
        presets[3] = new Color(139, 92, 246, 255);
        presets[4] = new Color(245, 158, 11, 255);
        presets[5] = new Color(6, 182, 212, 255);

        triangleGradientPicker = new TriangleGradientPicker(45, colorSetting);
        picker = new HueCirclePicker(43, 25, colorSetting);
    }

    @Override
    public void render(Quad quad) {
        this.quad = quad;

        int boxWidth = 14;
        int boxHeight = 14;
        int padding = 15;

        int targetX = quad.x + quad.width - boxWidth - padding;
        int targetY = quad.y + (35 - boxHeight) / 2;

        Quad headerQuad = new Quad(targetX, targetY, boxWidth, boxHeight);
        HybridRenderer2D.drawRoundRect(headerQuad, 3, 0, Color.RED, colorSetting.get());

        if (expanded) {
            Quad dropdownQuad = new Quad(
                    quad.x,
                    quad.y + 36,
                    quad.width,
                    this.height - 36
            );

            HybridRenderer2D.drawRoundRect(dropdownQuad, 10, 1, new Color(44, 47, 58, 255), new Color(26, 29, 41, 255));
            renderPresets();
            int offset = 8;
            int xOffset = 7;
            int cx = dropdownQuad.x + dropdownQuad.getWidth() - 50 + xOffset;

            picker.render(dropdownQuad.copy().addX(dropdownQuad.getWidth() - 50 + xOffset).addY(offset + 32));
            

            triangleGradientPicker.render(dropdownQuad.copy().addX(dropdownQuad.getWidth() - 73 + xOffset).addY(offset + 10));

            this.barQuad = dropdownQuad.copy().setWidth(4).setX(cx - 45).addY(11).subtractHeight(25);

            HybridRenderer2D.drawRoundRect(barQuad, 2, 0.5f, Color.GRAY, BASE_FILL);

            float alpha = colorSetting.get().getAlpha() / 255f;
            float progress = (alpha - 0.1f) / 0.9f;
            progress = Math.max(0f, Math.min(1f, progress));

            int progressHeight = (int) (barQuad.height * progress);

            if (progressHeight > 0) {
                Quad progressQuad = new Quad(
                        barQuad.x,
                        barQuad.y + barQuad.height - progressHeight,
                        barQuad.width,
                        progressHeight
                );
                HybridRenderer2D.drawRoundRect(progressQuad, 2, 0.5f, HOVER_ACTIVE_COLOR, HOVER_ACTIVE_COLOR);
            }

            float knobX = barQuad.x + (barQuad.width / 2f);
            float knobY = barQuad.y + barQuad.height - progressHeight;

            float targetColorWeight = isDraggingAlpha ? 1f : 0f;
            knobColorAnim = lerp(knobColorAnim, targetColorWeight, 0.15f);
            Color knobColor = blend(Color.WHITE, HOVER_ACTIVE_COLOR, knobColorAnim);

            HybridRenderer2D.drawCircle(
                    new Quad(Math.round(knobX - 4.9f), (int) (knobY - 5), 1, 1),
                    5.3f,
                    knobColor,
                    isDraggingAlpha
            );

            if (isDraggingAlpha) {
                String alphaTextStr = String.format("%d%%", Math.round(progress * 100f));

                HybridRenderText text = HybridTextRenderer.getTextRenderer(
                        alphaTextStr,
                        FontStyle.BOLD,
                        15,
                        Color.WHITE,
                        Color.WHITE,
                        false
                );

                text.setPosition((int) (knobX - 15 - (text.getWidth() / 2f)), (int) (knobY - 2));
                HybridTextRenderer.addText(text);
            }
        }

        super.render(quad);
    }

    public void renderPresets() {
        int presetSize = 16;
        int gap = 8;
        int rightPadding = 15;
        int topPadding = 12;

        int startX = quad.x + rightPadding;
        int startY = quad.y + 36 + topPadding;

        int lastY = startY;

        for (int i = 0; i < presets.length; i++) {
            int col = i % 3;
            int row = i / 3;

            int renderX = startX + (col * (presetSize + gap));
            int renderY = startY + (row * (presetSize + gap));

            Quad presetQuad = new Quad(renderX, renderY, presetSize, presetSize);
            HybridRenderer2D.drawRoundRect(presetQuad, 4, 0, presets[i], presets[i]);

            if (renderY + presetSize > lastY) {
                lastY = renderY + presetSize;
            }
        }

        int buttonWidth = (3 * presetSize) + (2 * gap);
        int buttonHeight = 14;
        int buttonGap = 10;

        int btnX = startX;
        int btnY = lastY + buttonGap;

        Quad buttonQuad = new Quad(btnX, btnY, buttonWidth, buttonHeight);

        Color btnBg = new Color(30, 34, 48, 255);
        HybridRenderer2D.drawRoundRect(buttonQuad, 4, 0, Color.GRAY, btnBg);


        boolean isCurrentlyDragging = isDraggingAlpha || picker.isDragging || triangleGradientPicker.isDragging;


        if (!isCurrentlyDragging || cachedRenderText == null) {
            Color current = colorSetting.get();
            String currentStr = String.format("RGB(%d, %d, %d)", current.getRed(), current.getGreen(), current.getBlue());

            if (!currentStr.equals(cachedRgbString) || cachedRenderText == null) {
                cachedRgbString = currentStr;
                cachedRenderText = HybridTextRenderer.getTextRenderer(cachedRgbString, FontStyle.REGULAR, 14, Color.WHITE);
            }
        }

        if (cachedRenderText != null) {
            int textWidth = cachedRenderText.getWidth();
            int textHeight = cachedRenderText.getHeight();

            int textX = btnX + (buttonWidth - textWidth) / 2;
            int textY = btnY + (buttonHeight - textHeight) / 2;

            cachedRenderText.setPosition(textX, textY);
            HybridTextRenderer.addText(cachedRenderText);
        }
    }

    @Override
    public void mouseDragged(MouseButtonEvent mouseButtonEvent) {
        if (expanded && isDraggingAlpha && barQuad != null) {
            updateAlphaFromMouse((float) mouseButtonEvent.y());
        }
        picker.mouseDragged(mouseButtonEvent);
        triangleGradientPicker.mouseDragged(mouseButtonEvent);

        super.mouseDragged(mouseButtonEvent);
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        triangleGradientPicker.mouseClicked(event);
        picker.mouseClicked(event);
        if (quad == null) return;

        float mouseX = (float) event.x();
        float mouseY = (float) event.y();


        if (expanded && barQuad != null && event.button() == 0) {
            boolean overBar = mouseX >= (barQuad.x - 5) && mouseX <= (barQuad.x + barQuad.width + 5) &&
                    mouseY >= barQuad.y && mouseY <= barQuad.y + barQuad.height;
            if (overBar) {
                isDraggingAlpha = true;
                updateAlphaFromMouse(mouseY);
                super.mouseClicked(event);
                return;
            }
        }


        if (expanded && event.button() == 0) {
            int presetSize = 16;
            int gap = 8;
            int rightPadding = 15;
            int topPadding = 12;

            int startX = quad.x + rightPadding;
            int startY = quad.y + 36 + topPadding;

            for (int i = 0; i < presets.length; i++) {
                int col = i % 3;
                int row = i / 3;

                int renderX = startX + (col * (presetSize + gap));
                int renderY = startY + (row * (presetSize + gap));

                if (mouseX >= renderX && mouseX <= renderX + presetSize &&
                        mouseY >= renderY && mouseY <= renderY + presetSize) {

                    int currentAlpha = colorSetting.get().getAlpha();
                    Color chosenPreset = presets[i];

                    colorSetting.set(new Color(
                            chosenPreset.getRed(),
                            chosenPreset.getGreen(),
                            chosenPreset.getBlue(),
                            currentAlpha
                    ));

                    super.mouseClicked(event);
                    return;
                }
            }
        }


        float x = quad.x;
        float y = quad.y;
        float w = quad.width;
        float h = 36;

        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;

        if (hovered && event.button() == 0) {
            expanded = !expanded;
            this.height = expanded ? 120 : 36;

            if (onHeightChanged != null) {
                onHeightChanged.run();
            }
        }

        super.mouseClicked(event);
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            isDraggingAlpha = false;
        }
        triangleGradientPicker.mouseReleased(event);
        picker.mouseReleased(event);
        super.mouseReleased(event);
    }

    private void updateAlphaFromMouse(float mouseY) {
        if (barQuad == null) return;

        float distanceFormBottom = (barQuad.y + barQuad.height) - mouseY;
        float pct = distanceFormBottom / (float) barQuad.height;
        pct = Math.max(0f, Math.min(1f, pct));

        float targetAlphaVal = 0.1f + (pct * 0.9f);
        int alphaInt = Math.round(targetAlphaVal * 255f);

        Color current = colorSetting.get();
        Color updatedColor = new Color(current.getRed(), current.getGreen(), current.getBlue(), alphaInt);
        colorSetting.set(updatedColor);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private Color blend(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int b2 = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, b2);
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}