package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.NumberSetting;
import hybrid.api.mod.settings.Setting;
import hybrid.api.theme.ThemeManager;
import hybrid.api.theme.ThemeTarget;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;




import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class NumberComponent extends CategoryComponent {

    private static final Color BASE_FILL = new Color(36, 41, 54, 255);

    private final NumberSetting numberSetting;
    private Quad bar;
    private Quad clickableKnobHitbox;

    private boolean dragging;


    private float animPercent = 0f;
    private float knobColorAnim = 0f;

    public NumberComponent(Setting<?> setting) {
        super(setting);
        this.numberSetting = (NumberSetting) setting;


        double percent = (numberSetting.get() - numberSetting.getMin()) / (numberSetting.getMax() - numberSetting.getMin());
        this.animPercent = (float) Math.max(0.0, Math.min(1.0, percent));
    }


    @Override
    public int getHeight() {
        return 36;
    }

    @Override
    public void render(Quad quad) {
        super.render(quad);



        int w = 80;
        int h = 4;

        int x = (int) (quad.getX() + quad.getWidth() - w) - 18;
        int y = (int) (quad.getY() + (quad.getHeight() - h) / 2f);

        bar = new Quad(x, y, w, h);


        double targetPercent = (numberSetting.get() - numberSetting.getMin()) / (numberSetting.getMax() - numberSetting.getMin());
        targetPercent = Math.max(0.0, Math.min(1.0, targetPercent));


        animPercent = lerp(animPercent, (float) targetPercent, 0.22f);
        if (Math.abs(animPercent - targetPercent) < 0.001f) {
            animPercent = (float) targetPercent;
        }


        HybridRenderer2D.drawRoundRect(bar, BASE_FILL, Color.GRAY, 2, 0.5f);


        float fillWidth = w * animPercent;
        if (fillWidth > 0) {
            Quad fillTrack = new Quad(x, y, Math.round(fillWidth), h);
            HybridRenderer2D.drawRoundRect(fillTrack, ThemeManager.get(ThemeTarget.ACCENT), ThemeManager.get(ThemeTarget.ACCENT), 2, 0.5f);
        }


        float knobX = x + fillWidth;
        float knobY = y + (h / 2f);


        int clickRadius = 14;
        clickableKnobHitbox = new Quad(
                Math.round(knobX - 7),
                Math.round(knobY - 7),
                clickRadius,
                clickRadius
        );


        float targetColorWeight = dragging ? 1f : 0f;
        knobColorAnim = lerp(knobColorAnim, targetColorWeight, 0.15f);
        Color knobColor = blend(Color.WHITE, ThemeManager.get(ThemeTarget.ACCENT), knobColorAnim);


        HybridRenderer2D.drawCircle(
                new Quad(Math.round(knobX - 3), (int) (knobY - 5), 1, 1),
                5.3f,
                knobColor,
                dragging
        );



        if (dragging) {
            String settingText = String.format("%.1f", numberSetting.get());

            HybridRenderText text = HybridTextRenderer.getTextRenderer(
                    settingText,
                    FontStyle.BOLD,
                    15,
                    Color.WHITE,
                    Color.WHITE,
                    false
            );



            text.setPosition((int) (knobX - (text.getWidth() / 2f)), (int) (knobY - 15));

            HybridTextRenderer.addText(text);
        }
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (event.button() != 0 || bar == null || clickableKnobHitbox == null) return;

        if (isInside(event.x(), event.y(), clickableKnobHitbox) || isInside(event.x(), event.y(), bar)) {
            dragging = true;
            update(event.x());
        }
    }

    @Override
    public void mouseDragged(MouseButtonEvent event) {
        if (!dragging || bar == null) return;
        update(event.x());
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0) {
            dragging = false;
        }
    }

    private void update(double mouseX) {
        double percent = (mouseX - bar.getX()) / bar.getWidth();
        percent = Math.max(0.0, Math.min(1.0, percent));

        double value = numberSetting.getMin() + percent * (numberSetting.getMax() - numberSetting.getMin());
        numberSetting.set((float) value);
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