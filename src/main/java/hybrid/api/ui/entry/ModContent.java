package hybrid.api.ui.entry;

import hybrid.api.ui.components.UIComponent;
import hybrid.api.ui.theme.GuiTheme;
import hybrid.api.util.render.Quad;

import static hybrid.api.Main.mc;

public class ModContent extends UIComponent {

    GuiTheme theme;


    public ModContent(GuiTheme theme) {
        this.theme = theme;
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        assert mc.screen != null;

        int screenW = mc.screen.width;
        int screenH = mc.screen.height;

        int mainW = 1000;
        int mainX = (screenW - mainW) / 2;
        int mainH = 800;
        int mainY = (screenH - mainH) / 2;

        Quad main = new Quad(mainX, mainY, mainW, mainH);

        theme.renderModsBackground(main);

        int sideW = 80;
        Quad side = new Quad(main.x, main.y, sideW, main.height);
        theme.renderSidePanel(side);

        super.render(mouseX, mouseY, tickDelta);
    }
}