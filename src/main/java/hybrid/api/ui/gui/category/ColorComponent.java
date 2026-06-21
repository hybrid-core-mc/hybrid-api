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
    private static final Color DROPDOWN_BG = new Color(26, 29, 41, 255);
    private static final Color DROPDOWN_BORDER = new Color(44, 47, 58, 255);
    private static final Color BTN_BG = new Color(30, 34, 48, 255);

    ColorSetting colorSetting;
    Runnable onHeightChanged;
    TriangleGradientPicker triangleGradientPicker;
    HueCirclePicker picker;

    Color[] presets;
    Quad[] presetQuads;

    Quad dropdownQuad = new Quad(0, 0, 0, 0),
            barQuad = new Quad(0, 0, 0, 0),
            workQuad = new Quad(0, 0, 0, 0),
            rootQuad;

    int height = 36;
    boolean expanded, isDraggingAlpha;
    float knobColorAnim;

    String cachedRgbString = "";
    HybridRenderText cachedRenderText;

    public ColorComponent(Setting<?> setting, Runnable onHeightChanged) {
        super(setting);
        this.colorSetting = (ColorSetting) setting;
        this.onHeightChanged = onHeightChanged;

        this.presets = new Color[]{
                new Color(99, 102, 241),
                new Color(16, 185, 129),
                new Color(244, 63, 94),
                new Color(139, 92, 246),
                new Color(245, 158, 11),
                new Color(6, 182, 212)
        };

        this.presetQuads = new Quad[presets.length];
        for (int i = 0; i < presets.length; i++) {
            presetQuads[i] = new Quad(0, 0, 16, 16);
        }

        this.triangleGradientPicker = new TriangleGradientPicker(45, colorSetting);
        this.picker = new HueCirclePicker(43, 25, colorSetting);
    }
    @Override
    public void render(Quad quad) {
        this.rootQuad = quad;

        int targetX = quad.x + quad.width - 14 - 15;
        int targetY = quad.y + (35 - 14) / 2;

        workQuad.set(targetX, targetY, 14, 14);
        HybridRenderer2D.drawRoundRect(workQuad, colorSetting.get(), Color.RED, 3, 0);

        if (expanded) {
            dropdownQuad.set(quad.x, quad.y + 36, quad.width, this.height - 36);
            HybridRenderer2D.drawRoundRect(dropdownQuad, DROPDOWN_BG, DROPDOWN_BORDER, 10, 1);

            recalculateAndRenderPresets();

            int cx = dropdownQuad.x + dropdownQuad.width - 50 + 7;

            workQuad.set(dropdownQuad).addX(dropdownQuad.width - 50 + 7).addY(8 + 32);
            picker.render(workQuad);


            workQuad.set(dropdownQuad).addX(dropdownQuad.width - 73 + 7).addY(8 + 10);
            triangleGradientPicker.render(workQuad);


            this.barQuad.set(dropdownQuad).setWidth(4).setX(cx - 45).addY(11).subtractHeight(25);
            HybridRenderer2D.drawRoundRect(barQuad, BASE_FILL, Color.GRAY, 2, 0.5f);

            float alpha = colorSetting.get().getAlpha() / 255f;
            float progress = Math.max(0f, Math.min(1f, (alpha - 0.1f) / 0.9f));
            int progressHeight = (int) (barQuad.height * progress);

            if (progressHeight > 0) {
                workQuad.set(barQuad.x, barQuad.y + barQuad.height - progressHeight, barQuad.width, progressHeight);
                HybridRenderer2D.drawRoundRect(workQuad, HOVER_ACTIVE_COLOR, HOVER_ACTIVE_COLOR, 2, 0.5f);
            }

            float knobX = barQuad.x + (barQuad.width / 2f);
            float knobY = barQuad.y + barQuad.height - progressHeight;

            knobColorAnim = knobColorAnim + ((isDraggingAlpha ? 1f : 0f) - knobColorAnim) * 0.15f;
            Color knobColor = blend(knobColorAnim);

            workQuad.set(Math.round(knobX - 4.9f), (int) (knobY - 5), 1, 1);
            HybridRenderer2D.drawCircle(workQuad, 5.3f, knobColor, isDraggingAlpha);

            if (isDraggingAlpha) {
                String alphaTextStr = Math.round(progress * 100f) + "%";
                HybridRenderText text = HybridTextRenderer.getTextRenderer(alphaTextStr, FontStyle.BOLD, 15, Color.WHITE, Color.WHITE, false);
                text.setPosition((int) (knobX - 15 - (text.getWidth() / 2f)), (int) (knobY - 2));
                HybridTextRenderer.addText(text);
            }
        }

        super.render(quad);
    }

    private void recalculateAndRenderPresets() {
        int startX = rootQuad.x + 15;
        int startY = rootQuad.y + 36 + 12;
        int lastY = startY;

        for (int i = 0; i < presets.length; i++) {
            int col = i % 3;
            int row = i / 3;

            int renderX = startX + (col * 24);
            int renderY = startY + (row * 24);

            presetQuads[i].setX(renderX).setY(renderY);
            HybridRenderer2D.drawRoundRect(presetQuads[i], presets[i], presets[i], 4, 0);

            if (renderY + 16 > lastY) {
                lastY = renderY + 16;
            }
        }

        int buttonWidth = 64;
        int buttonHeight = 14;
        int btnY = lastY + 10;

        workQuad.set(startX, btnY, buttonWidth, buttonHeight);
        HybridRenderer2D.drawRoundRect(workQuad, BTN_BG, Color.GRAY, 4, 0);

        boolean isDragging = isDraggingAlpha || picker.isDragging || triangleGradientPicker.isDragging;

        if (!isDragging || cachedRenderText == null) {
            Color current = colorSetting.get();
            String currentStr = "RGB(" + current.getRed() + ", " + current.getGreen() + ", " + current.getBlue() + ")";

            if (!currentStr.equals(cachedRgbString) || cachedRenderText == null) {
                cachedRgbString = currentStr;
                cachedRenderText = HybridTextRenderer.getTextRenderer(cachedRgbString, FontStyle.REGULAR, 14, Color.WHITE);
            }
        }

        int textX = startX + (buttonWidth - cachedRenderText.getWidth()) / 2;
        int textY = btnY + (buttonHeight - cachedRenderText.getHeight()) / 2;
        cachedRenderText.setPosition(textX, textY);
        HybridTextRenderer.addText(cachedRenderText);
    }

    @Override
    public void mouseDragged(MouseButtonEvent event) {
        if (expanded && isDraggingAlpha) {
            updateAlphaFromMouse((float) event.y());
        }
        picker.mouseDragged(event);
        triangleGradientPicker.mouseDragged(event);
        super.mouseDragged(event);
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        triangleGradientPicker.mouseClicked(event);
        picker.mouseClicked(event);
        if (rootQuad == null) return;

        float mouseX = (float) event.x();
        float mouseY = (float) event.y();

        if (expanded && event.button() == 0) {

            if (mouseX >= (barQuad.x - 5) && mouseX <= (barQuad.x + barQuad.width + 5) &&
                    mouseY >= barQuad.y && mouseY <= barQuad.y + barQuad.height) {
                isDraggingAlpha = true;
                updateAlphaFromMouse(mouseY);
                super.mouseClicked(event);
                return;
            }


            for (int i = 0; i < presets.length; i++) {
                Quad q = presetQuads[i];
                if (mouseX >= q.x && mouseX <= q.x + 16 && mouseY >= q.y && mouseY <= q.y + 16) {
                    colorSetting.set(new Color(presets[i].getRed(), presets[i].getGreen(), presets[i].getBlue(), colorSetting.get().getAlpha()));
                    super.mouseClicked(event);
                    return;
                }
            }
        }


        if (event.button() == 0 && mouseX >= rootQuad.x && mouseX <= rootQuad.x + rootQuad.width && mouseY >= rootQuad.y && mouseY <= rootQuad.y + 36) {
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
        float pct = ((barQuad.y + barQuad.height) - mouseY) / (float) barQuad.height;
        pct = Math.max(0f, Math.min(1f, pct));

        int alphaInt = Math.round((0.1f + (pct * 0.9f)) * 255f);
        Color current = colorSetting.get();
        colorSetting.set(new Color(current.getRed(), current.getGreen(), current.getBlue(), alphaInt));
    }

    private Color blend(float t) {
        float clampedT = Math.max(0f, Math.min(1f, t));
        return new Color(
                (int) (Color.WHITE.getRed() + (ColorComponent.HOVER_ACTIVE_COLOR.getRed() - Color.WHITE.getRed()) * clampedT),
                (int) (Color.WHITE.getGreen() + (ColorComponent.HOVER_ACTIVE_COLOR.getGreen() - Color.WHITE.getGreen()) * clampedT),
                (int) (Color.WHITE.getBlue() + (ColorComponent.HOVER_ACTIVE_COLOR.getBlue() - Color.WHITE.getBlue()) * clampedT)
        );
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}