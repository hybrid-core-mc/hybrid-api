package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.ColorSetting;
import hybrid.api.mod.settings.Setting;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.*;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ColorComponent extends CategoryComponent {
    private final ColorSetting colorSetting;
    private int height = 36;
    private boolean expanded = false;
    private final Runnable onHeightChanged;
    private Quad quad;
    private final Color[] presets;
    TriangleGradientPicker triangleGradientPicker;
    HueCirclePicker picker;

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
        triangleGradientPicker = new TriangleGradientPicker(45);
        picker = new HueCirclePicker(43,colorSetting);
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
            picker.draw(dropdownQuad.x + dropdownQuad.getWidth()-50+xOffset, dropdownQuad.y + 32+offset, colorSetting.get());
            triangleGradientPicker.render(RenderContext.get(),0,0,dropdownQuad.x + dropdownQuad.getWidth()-73+xOffset, dropdownQuad.y + 10+offset,0.5f);
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

        String textStr = String.format("RGB(%d, %d, %d)", colorSetting.get().getRed(), colorSetting.get().getGreen(), colorSetting.get().getBlue());
        HybridRenderText x = HybridTextRenderer.getTextRenderer(textStr, FontStyle.REGULAR, 14, Color.WHITE);


        int textWidth = x.getWidth();
        int textHeight = x.getHeight(); 

        int textX = btnX + (buttonWidth - textWidth) / 2;
        int textY = btnY + (buttonHeight - textHeight) / 2;

        x.setPosition(textX, textY);
        HybridTextRenderer.addText(x);
    }

    @Override
    public void mouseDragged(MouseButtonEvent mouseButtonEvent) {
        picker.mouseClicked(mouseButtonEvent);
        super.mouseDragged(mouseButtonEvent);
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (quad == null) return;

        triangleGradientPicker.mouseClicked(event);

        float mouseX = (float) event.x();
        float mouseY = (float) event.y();

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
    public int getHeight() {
        return this.height;
    }
}