package hybrid.api.ui.gui.normal;

import hybrid.api.mod.HybridMod;
import hybrid.api.mod.HybridMods;
import hybrid.api.mod.SprintMod;
import hybrid.api.mod.ThemeMod;
import hybrid.api.theme.ThemeManager;
import hybrid.api.theme.ThemeTarget;
import hybrid.api.ui.gui.pages.DefaultSettingsPage;
import hybrid.api.ui.gui.parts.GuiPart;
import hybrid.api.ui.gui.parts.SidebarPart;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;
import hybrid.api.util.render.RenderContext;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class DefaultGui extends GuiPart {

    private final SidebarPart sidebarPart = new DefaultSidebar();

    DefaultSettingsPage modSettingsPage;

    public DefaultGui() {
        modSettingsPage = new DefaultSettingsPage(HybridMods.getMod(ThemeMod.class));
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta, HybridMod mod, int screenWidth, int screenHeight) {

        int w = (int) (930 * 0.58);
        int h = (int) (490 * 0.65);

        Quad background = new Quad((screenWidth - w) / 2, (screenHeight - h) / 2, w, h);



        int settingsPageWidth = (int) (background.getWidth() * 0.75);
        Quad sidebarBounds = background.copy().subtractWidth(settingsPageWidth);

        HybridRenderer2D.drawRoundRect(background.copy().setWidth(settingsPageWidth).addX(sidebarBounds.width), ThemeManager.get(ThemeTarget.MAIN_BG), ThemeManager.get(ThemeTarget.BORDER), 10, 1.5f,10,10,0,0);
        HybridRenderer2D.drawRoundRect(sidebarBounds, ThemeManager.get(ThemeTarget.SIDEBAR_BG), ThemeManager.get(ThemeTarget.BORDER), 10, 1.5f, 0, 0, 10, 10);
        int leftPadding = 24;
        int dotSize = 6;
        int logoX = sidebarBounds.x + leftPadding;
        int alignmentX = logoX + dotSize + 10;

        RenderContext.get().enableScissor(background.x, background.y, background.x + background.width,background.y+ background.height);
        sidebarPart.renderSidebar(sidebarBounds, alignmentX,mouseX,mouseY);

        modSettingsPage.render(background.copy().setWidth(settingsPageWidth).setX(sidebarBounds.x + sidebarBounds.width));

        HybridTextRenderer.render(RenderContext.get());
        RenderContext.get().disableScissor();


    }


    @Override
    public void mouseScrolled(double d, double e, double f, double g) {
        sidebarPart.mouseScrolled(d, e, f, g);
        modSettingsPage.mouseScrolled(d, e, f, g);
        super.mouseScrolled(d, e, f, g);
    }

    @Override
    public void mouseClicked(MouseButtonEvent mouseButtonEvent) {
        sidebarPart.mouseClicked(mouseButtonEvent);
        modSettingsPage.mouseClicked(mouseButtonEvent);
        super.mouseClicked(mouseButtonEvent);
    }

    @Override
    public void mouseReleased(MouseButtonEvent mouseButtonEvent) {
        sidebarPart.mouseReleased(mouseButtonEvent);
        modSettingsPage.mouseReleased(mouseButtonEvent);
        super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public void mouseDragged(MouseButtonEvent mouseButtonEvent) {
        sidebarPart.mouseDragged(mouseButtonEvent);
        modSettingsPage.mouseDragged(mouseButtonEvent);
        super.mouseDragged(mouseButtonEvent);
    }
}