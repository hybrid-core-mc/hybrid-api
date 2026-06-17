package hybrid.api.ui.entry;

import hybrid.api.ui.components.UIComponent;
import hybrid.api.ui.theme.GuiTheme;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;

public class ModContent extends UIComponent {

    GuiTheme theme;

    public ModContent(GuiTheme theme) {
        this.theme = theme;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        theme.renderModsBackground(new Quad(0, 0, 100, 100));

        super.render(mouseX, mouseY, tickDelta);
    }
}
