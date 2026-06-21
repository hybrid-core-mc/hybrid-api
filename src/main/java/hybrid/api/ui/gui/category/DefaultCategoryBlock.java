package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.*;
import hybrid.api.ui.gui.GuiEvents;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultCategoryBlock extends GuiEvents {

    private static final int HEADER_HEIGHT = 44;
    private static final int PADDING = 4;
    private static final int SPACING = 6;

    private static final Color BORDER = new Color(35, 36, 45);
    private static final Color BG = new Color(19, 21, 29);
    private static final Color CONTENT_BG = new Color(16, 18, 26);
    private static final Color TEXT = Color.WHITE;

    private final BuiltCategory category;
    private final List<CategoryComponent> components = new ArrayList<>();

    private boolean expanded = false;
    private Quad bounds;
    private int targetHeight;
    private float animHeight;

    public DefaultCategoryBlock(BuiltCategory category) {
        this.category = category;

        
        Runnable heightTicker = this::recalcHeight;

        for (Setting<?> s : category.getSettings()) {
            if (s instanceof ColorSetting c) {
                components.add(new ColorComponent(c, heightTicker));
            }
            if (s instanceof BooleanSetting b) {
                components.add(new BooleanComponent(b)); 
            }
            if (s instanceof NumberSetting n) {
                components.add(new NumberComponent(n)); 
            }
            if(s instanceof ModeSetting<?> m) {
                components.add(new ModeComponent(m));
            }
        }

        recalcHeight();
        this.animHeight = targetHeight;
    }

    public int getHeight() {
        return Math.round(animHeight);
    }

    public void render(Quad quad) {
        this.bounds = quad;

        int currentTarget = expanded ? targetHeight : 34;

        animHeight = animHeight + (currentTarget - animHeight) * 0.20f;
        if (Math.abs(animHeight - currentTarget) < 0.5f) {
            animHeight = currentTarget;
        }

        int currentHeight = Math.round(animHeight);

        Quad box = new Quad(
                quad.getX(),
                quad.getY(),
                quad.getWidth(),
                currentHeight
        );

        HybridRenderer2D.drawRoundRect(
                box,
                10,
                1.5f,
                BORDER,
                new Color(21, 23, 31, 255)
        );

        HybridRenderText title = HybridTextRenderer.getTextRenderer(
                category.getName(),
                FontStyle.BOLD,
                18,
                TEXT,
                TEXT,
                false
        );

        title.setPosition(quad.getX() + 16, quad.getY() + 13);
        HybridTextRenderer.addText(title);

        
        if (currentHeight <= HEADER_HEIGHT + PADDING) return;

        int stripY = quad.getY() + 35;
        
        int stripHeight = currentHeight - 32 - PADDING;

        Quad strip = new Quad(
                quad.getX(),
                stripY ,
                quad.getWidth() - 1,
                stripHeight
        );

        HybridRenderer2D.drawRoundRect(
                strip,
                0,
                0,
                new Color(0, 58, 255),
                new Color(18, 20, 27, 255),
                10, 0, 10, 0
        );

        int y = quad.getY() + HEADER_HEIGHT + PADDING;

        for (CategoryComponent c : components) {
            int h = c.getHeight();

            if (y + h <= quad.getY() + currentHeight - PADDING && y >= quad.getY() + HEADER_HEIGHT) {
                Quad q = new Quad(
                        quad.getX() + 12,
                        y,
                        quad.getWidth() - 24,
                        h
                );

                c.render(q);
            }

            y += h + SPACING;
        }
    }

    @Override
    public void mouseClicked(MouseButtonEvent event) {
        if (bounds == null) return;

        double mx = event.x();
        double my = event.y();

        Quad header = new Quad(
                bounds.getX(),
                bounds.getY(),
                bounds.getWidth(),
                HEADER_HEIGHT
        );

        if (isInside(mx, my, header)) {
            if (event.button() == 0) {
                expanded = !expanded;
                recalcHeight();
            }
            return;
        }

        if (!expanded || animHeight <= HEADER_HEIGHT) return;

        int y = bounds.getY() + HEADER_HEIGHT + PADDING;

        for (CategoryComponent c : components) {
            int h = c.getHeight();

            
            if (my >= bounds.getY() + HEADER_HEIGHT && my <= bounds.getY() + animHeight - PADDING) {
                if (my >= y && my <= y + h) {
                    c.mouseClicked(event);
                }
            }
            y += h + SPACING;
        }
    }

    @Override
    public void mouseDragged(MouseButtonEvent event) {
        if (bounds == null || !expanded || animHeight <= HEADER_HEIGHT) return;

        for (GuiEvents c : components) {
            c.mouseDragged(event);
        }
    }

    @Override
    public void mouseReleased(MouseButtonEvent event) {
        if (bounds == null || !expanded) return;

        for (GuiEvents c : components) {
            c.mouseReleased(event);
        }
        recalcHeight();
    }

    private void recalcHeight() {
        targetHeight = HEADER_HEIGHT;

        if (expanded) {
            for (CategoryComponent c : components) {
                targetHeight += c.getHeight();
                targetHeight += SPACING;
            }
            targetHeight += PADDING * 2;
        }
    }

    private boolean isInside(double mx, double my, Quad q) {
        return mx >= q.getX() && mx <= q.getX() + q.getWidth()
                && my >= q.getY() && my <= q.getY() + q.getHeight();
    }
}