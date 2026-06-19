package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.BooleanSetting;
import hybrid.api.mod.settings.Setting;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class BooleanComponent extends CategoryComponent {

    private static final Color ON_COLOR = new Color(99, 102, 241);
    private static final Color OFF_COLOR = new Color(51, 65, 85);
    private static final Color KNOB_COLOR = Color.WHITE;

    private final BooleanSetting setting;
    private Quad bounds;

    private float anim;

    
    private boolean lastState;

    public BooleanComponent(Setting<?> setting) {
        super(setting);
        this.setting = (BooleanSetting) setting;
        this.lastState = ((BooleanSetting) setting).isEnabled();
        this.anim = lastState ? 1f : 0f;
    }

    public int height() {
        return 32;
    }

    @Override
    public void render(Quad quad) {
        this.bounds = quad;

        boolean enabled = setting.isEnabled();

        if (enabled != lastState) {
            lastState = enabled;
        }

        float target = lastState ? 1f : 0f;

        
        anim = lerp(anim, target, 0.18f);
        if (Math.abs(anim - target) < 0.001f) {
            anim = target;
        }

        float x = quad.getX() + quad.getWidth() - 42;

        float y = quad.getY() + 6;

        float width = 26f;
        float height = 13.3f;

        
        Quad toggle = new Quad(Math.round(x), Math.round(y), Math.round(width), Math.round(height));

        Color bg = blend(OFF_COLOR, ON_COLOR, anim);

        HybridRenderer2D.drawRoundRect(toggle, 6, 0, bg, bg);

        float knobSize = 12f;
        float travel = width - knobSize-2 ;

        float knobX = x-5 + (anim * travel);
        float knobY = y -5f;

        float cx = knobX + knobSize / 2f;
        float cy = knobY + knobSize / 2f;

        
        HybridRenderer2D.drawCircle(
                new Quad(Math.round(cx), Math.round(cy), 1, 1),
                5.3f,
                KNOB_COLOR,
                false
        );

        super.render(quad);
    }
    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (bounds == null) return;
        if (event.button() != 0) return;

        Quad toggle = new Quad(
                bounds.getX() + bounds.getWidth() - 42,
                bounds.getY() + 6,
                26,
                13
        );

        if (isInside(event.x(), event.y(), toggle)) {
            setting.toggle();
        }
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

    private boolean isInside(double mx, double my, Quad q) {
        return mx >= q.getX() && mx <= q.getX() + q.getWidth()
                && my >= q.getY() && my <= q.getY() + q.getHeight();
    }
}