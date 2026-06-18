package hybrid.api.ui.gui.normal;

import hybrid.api.mod.settings.BooleanSetting;
import hybrid.api.mod.settings.BuiltCategory;
import hybrid.api.mod.settings.NumberSetting;
import hybrid.api.mod.settings.Setting;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DefaultCategoryBlock {

    private static final Color BORDER_COLOR = new Color(35, 36, 45);
    private static final Color BG_COLOR = new Color(19, 21, 29);
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color SETTING_COLOR = new Color(150, 160, 175);

    // Header height constant for clean alignment across logic and rendering
    private static final int HEADER_HEIGHT = 44;

    private final BuiltCategory category;
    private final List<Component> componentList = new ArrayList<>();

    private boolean expanded = true;
    private int height;
    private Quad bounds;

    public DefaultCategoryBlock(BuiltCategory category) {
        this.category = category;

        for (Setting<?> setting : category.getSettings()) {
            Component component = createComponent(setting);
            if (component != null) {
                componentList.add(component);
            }
        }
        recalculateHeight();
    }

    public int getHeight() {
        return height;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        if (bounds == null) return false;
        return isInside(mouseX, mouseY, bounds);
    }

    public void toggle() {
        expanded = !expanded;
        recalculateHeight();
    }

    private void recalculateHeight() {
        height = HEADER_HEIGHT;
        if (expanded) {
            for (Component c : componentList) {
                height += c.height() + 6;
            }
        }
        height += 6; // Padding bottom
    }

    public void render(Quad quad) {
        this.bounds = quad;

        // Draw outer frame
        HybridRenderer2D.drawRoundRect(quad, 10, 1.5f, BORDER_COLOR, BG_COLOR);

        // Render clean Category Title
        HybridRenderText title = HybridTextRenderer.getTextRenderer(
                category.getName() + (expanded ? "  -" : "  +"),
                FontStyle.BOLD,
                18,
                TITLE_COLOR,
                TITLE_COLOR,
                false
        );
        title.setPosition(quad.getX() + 16, quad.getY() + 13);
        HybridTextRenderer.addText(title);

        if (!expanded) return;

        // Layout settings exactly matching event handlers
        int y = quad.getY() + HEADER_HEIGHT;
        for (Component c : componentList) {
            Quad settingQuad = new Quad(
                    quad.getX() + 12,
                    y,
                    quad.getWidth() - 24,
                    c.height()
            );
            c.render(settingQuad);
            y += c.height() + 6;
        }
    }

    // =========================
    // EVENT HANDLING SYSTEM
    // =========================

    public void mouseClick(MouseButtonEvent event) {
        if (bounds == null) return;

        // Toggle Expand/Collapse cleanly using the exact header segment
        Quad headerQuad = new Quad(bounds.getX(), bounds.getY(), bounds.getWidth(), HEADER_HEIGHT);
        if (isInside(event.x(), event.y(), headerQuad)) {
            if (event.button() == 0) {
                toggle();
            }
            return;
        }

        if (!expanded) return;

        int y = bounds.getY() + HEADER_HEIGHT;
        for (Component c : componentList) {
            Quad settingQuad = new Quad(bounds.getX() + 12, y, bounds.getWidth() - 24, c.height());
            c.mouseClicked(event.x(), event.y(), event.button(), settingQuad);
            y += c.height() + 6;
        }
    }

    public void mouseRelease(MouseButtonEvent event) {
        if (!expanded || bounds == null) return;

        int y = bounds.getY() + HEADER_HEIGHT;
        for (Component c : componentList) {
            Quad settingQuad = new Quad(bounds.getX() + 12, y, bounds.getWidth() - 24, c.height());
            c.mouseReleased(event.x(), event.y(), event.button(), settingQuad);
            y += c.height() + 6;
        }
    }

    private static boolean isInside(double mx, double my, Quad q) {
        return mx >= q.getX() && mx <= q.getX() + q.getWidth() &&
                my >= q.getY() && my <= q.getY() + q.getHeight();
    }

    private Component createComponent(Setting<?> setting) {
        if (setting instanceof BooleanSetting b) return new BooleanComponent(b);
        if (setting instanceof NumberSetting n) return new NumberComponent(n);
        return null;
    }

    // =========================
    // COMPONENT SYSTEM
    // =========================

    private abstract static class Component {
        abstract int height();
        abstract void render(Quad quad);
        void mouseClicked(double mouseX, double mouseY, int button, Quad quad) {}
        void mouseReleased(double mouseX, double mouseY, int button, Quad quad) {}
    }

    private static class BooleanComponent extends Component {
        private final BooleanSetting setting;

        public BooleanComponent(BooleanSetting setting) {
            this.setting = setting;
        }

        @Override
        int height() { return 32; }

        @Override
        void render(Quad quad) {
            HybridRenderer2D.drawRoundRect(quad, 6, 1, new Color(42, 44, 56), new Color(24, 26, 34));

            HybridRenderText text = HybridTextRenderer.getTextRenderer(
                    setting.getName(), FontStyle.REGULAR, 14, SETTING_COLOR, SETTING_COLOR, false
            );
            text.setPosition(quad.getX() + 10, quad.getY() + 8);
            HybridTextRenderer.addText(text);

            Quad toggle = new Quad(quad.getX() + quad.getWidth() - 42, quad.getY() + 6, 24, 20);
            Color toggleColor = setting.isEnabled() ? new Color(90, 170, 255) : new Color(70, 70, 70);
            HybridRenderer2D.drawRoundRect(toggle, 5, 1, toggleColor, toggleColor);
        }

        @Override
        void mouseClicked(double mouseX, double mouseY, int button, Quad quad) {
            if (button == 0 && isInside(mouseX, mouseY, quad)) {
                setting.toggle();
            }
        }
    }

    private static class NumberComponent extends Component {
        private final NumberSetting setting;
        private boolean dragging = false;

        public NumberComponent(NumberSetting setting) {
            this.setting = setting;
        }

        @Override
        int height() { return 42; }

        @Override
        void render(Quad quad) {
            HybridRenderer2D.drawRoundRect(quad, 6, 1, new Color(42, 44, 56), new Color(24, 26, 34));

            // Dynamic number tracking text
            String titleText = setting.getName() + ": " + String.format("%.2f", setting.get().doubleValue());
            HybridRenderText text = HybridTextRenderer.getTextRenderer(
                    titleText, FontStyle.REGULAR, 14, SETTING_COLOR, SETTING_COLOR, false
            );
            text.setPosition(quad.getX() + 10, quad.getY() + 6);
            HybridTextRenderer.addText(text);

            Quad bar = new Quad(quad.getX() + 10, quad.getY() + 26, quad.getWidth() - 20, 4);
            HybridRenderer2D.drawRoundRect(bar, 2, 0, new Color(60, 60, 60), new Color(60, 60, 60));

            // Percentage Fill logic
            double min = setting.getMin();
            double max = setting.getMax();
            double current = setting.get().doubleValue();
            double percent = Math.min(1.0, Math.max(0.0, (current - min) / (max - min)));

            Quad fill = new Quad(bar.getX(), bar.getY(), (int) (bar.getWidth() * percent), bar.getHeight());
            HybridRenderer2D.drawRoundRect(fill, 2, 0, new Color(90, 170, 255), new Color(90, 170, 255));
        }

        @Override
        void mouseClicked(double mouseX, double mouseY, int button, Quad quad) {
            if (button == 0 && isInside(mouseX, mouseY, quad)) {
                this.dragging = true;
                updateSlider(mouseX, quad);
            }
        }

        @Override
        void mouseReleased(double mouseX, double mouseY, int button, Quad quad) {
            if (button == 0) {
                this.dragging = false;
            }
        }

        // Updates the numbers settings value cleanly when sliding
        private void updateSlider(double mouseX, Quad quad) {
            Quad bar = new Quad(quad.getX() + 10, quad.getY() + 26, quad.getWidth() - 20, 4);
            double percent = (mouseX - bar.getX()) / (double) bar.getWidth();
            percent = Math.min(1.0, Math.max(0.0, percent));

            double min = setting.getMin();
            double max = setting.getMax();
            double newValue = min + (percent * (max - min));

            setting.set((float) newValue);
        }
    }
}