package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.ModeSetting;
import hybrid.api.mod.settings.Setting;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ModeComponent extends CategoryComponent {


    private final ModeSetting<?> setting;
    private Quad bounds;

    public ModeComponent(Setting<?> setting) {
        super(setting);
        this.setting = (ModeSetting<?>) setting;
    }

    @Override
    public int getHeight() {
        return 32;
    }

    @Override
    public void render(Quad quad) {
        this.bounds = quad;

        float boxWidth = 65f;
        float boxHeight = 16f;
        float x = quad.getX() + quad.getWidth() - boxWidth - 12;
        float y = quad.getY() + (getHeight() - boxHeight) / 2f;

        Quad modeBox = new Quad(Math.round(x), Math.round(y), Math.round(boxWidth), Math.round(boxHeight));

        HybridRenderer2D.drawRoundRect(modeBox, 3, 0.3f, Color.GRAY, new Color(24, 27, 38, 255));

        String modeName = setting.getMode().name();
        HybridRenderText modeText = HybridTextRenderer.getTextRenderer(
                modeName,
                FontStyle.REGULAR,
                15,
                Color.WHITE,
                Color.BLUE,
                false
        );

        float textWidth = modeText.getWidth();
        float textHeight = modeText.getHeight();

        float centeredX = x + (boxWidth - textWidth) / 2f;
        float centeredY = y + (boxHeight - textHeight) / 2f;

        modeText.setPosition((int) centeredX, (int) centeredY);
        HybridTextRenderer.addText(modeText);

        super.render(quad);
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (bounds == null) return;
        if (event.button() != 0) return;

        float boxWidth = 65f;
        float boxHeight = 16f;
        Quad modeBox = new Quad(
                Math.round(bounds.getX() + bounds.getWidth() - boxWidth - 12),
                Math.round(bounds.getY() + (getHeight() - boxHeight) / 2f),
                Math.round(boxWidth),
                Math.round(boxHeight)
        );

        if (isInside(event.x(), event.y(), modeBox)) {
            setting.cycle();
        }
    }

    private boolean isInside(double mx, double my, Quad q) {
        return mx >= q.getX() && mx <= q.getX() + q.getWidth()
                && my >= q.getY() && my <= q.getY() + q.getHeight();
    }
}