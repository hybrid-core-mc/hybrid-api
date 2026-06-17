package hybrid.api.ui.entry;

import hybrid.api.ui.components.UIComponent;
import hybrid.api.ui.theme.GuiTheme;

public class ModController extends UIComponent {

    GuiTheme guiTheme;

    public ModController(GuiTheme guiTheme) {
        this.guiTheme = guiTheme;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        super.render(mouseX, mouseY, tickDelta);
    }
}
