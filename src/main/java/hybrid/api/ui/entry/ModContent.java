package hybrid.api.ui.entry;

import hybrid.api.ui.components.UIComponent;
import hybrid.api.ui.theme.GuiTheme;
import hybrid.api.util.render.Quad;

public class ModContent extends UIComponent {

    GuiTheme theme;

    public ModContent(GuiTheme theme) {
        this.theme = theme;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        Quad quad = new Quad(50, 50, 100, 100);
        theme.renderModsBackground(quad);

        Quad spliced = quad.copy(quad);
        spliced.x -= 30;
        spliced.width -= 50;
        theme.renderSidePanel(spliced);

        super.render(mouseX, mouseY, tickDelta);
    }
}
