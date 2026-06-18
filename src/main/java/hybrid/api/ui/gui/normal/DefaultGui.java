package hybrid.api.ui.gui.normal;

import hybrid.api.mod.HybridMod;
import hybrid.api.mod.SprintMod;
import hybrid.api.ui.gui.parts.GuiPart;
import hybrid.api.ui.gui.parts.SidebarPart;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;

import java.awt.*;

public class DefaultGui implements GuiPart {

    private final SidebarPart sidebarPart = new DefaultSidebar();

    DefaultSettingsPage modSettingsPage;
    public DefaultGui() {
        modSettingsPage = new DefaultSettingsPage(new SprintMod());
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight) {

        int w = (int) (960 * 0.58);
        int h = (int) (490 * 0.65);

        Quad background = new Quad((screenWidth - w) / 2, (screenHeight - h) / 2, w, h);
        Color border = new Color(44, 45, 56);

        HybridRenderer2D.drawRoundRect(background, 10, 1.5f, border, new Color(18, 20, 28));

        int settingsPageWidth = (int) (background.getWidth() * 0.75);
        Quad sidebarBounds = background.copy().subtractWidth(settingsPageWidth);

        int leftPadding = 24;
        int dotSize = 6;
        int logoX = sidebarBounds.x + leftPadding;
        int alignmentX = logoX + dotSize + 10;

        sidebarPart.renderSidebar(sidebarBounds, alignmentX);

        modSettingsPage.render(background.copy().setWidth(settingsPageWidth).setX(sidebarBounds.x + sidebarBounds.width));

    }
}