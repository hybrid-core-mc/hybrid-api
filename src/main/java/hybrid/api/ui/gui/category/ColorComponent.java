package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.ColorSetting;
import hybrid.api.mod.settings.Setting;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ColorComponent extends CategoryComponent {
    private final ColorSetting setting;
    private int height = 36; 
    private boolean expanded = false;
    private final Runnable onHeightChanged; 
    private Quad quad;

    public ColorComponent(Setting<?> setting, Runnable onHeightChanged) {
        super(setting);
        this.setting = (ColorSetting) setting;
        this.onHeightChanged = onHeightChanged;
    }

    @Override
    public void render(Quad quad) {
        this.quad = quad;
        
        HybridRenderer2D.drawRoundRect(quad, 10, 1, Color.RED, Color.PINK);
        super.render(quad);
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
        if (quad == null) return;

        float mouseX = (float) event.x();
        float mouseY = (float) event.y();

        float x = quad.x;
        float y = quad.y;
        float w = quad.width;
        float h = 36; 

        boolean hovered = mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h;

        if (hovered && event.button() == 0) {
            expanded = !expanded;
            
            this.height = expanded ? 100 : 36;

            System.out.println("ColorComponent expanded: " + expanded + " | New Height: " + this.height);

            
            if (onHeightChanged != null) {
                onHeightChanged.run();
            }
        }

        super.mouseReleased(event);
    }

    @Override
    public int getHeight() {
        return this.height;
    }
}