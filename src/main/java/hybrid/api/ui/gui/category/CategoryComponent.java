package hybrid.api.ui.gui.category;

import hybrid.api.mod.settings.Setting;
import hybrid.api.ui.gui.GuiEvents;
import hybrid.api.util.font.FontStyle;
import hybrid.api.util.font.HybridRenderText;
import hybrid.api.util.font.HybridTextRenderer;
import hybrid.api.util.render.HybridRenderer2D;
import hybrid.api.util.render.Quad;

import java.awt.*;

import static hybrid.api.ui.gui.normal.DefaultSettingsPage.DESC_IDLE_COLOR;
import static hybrid.api.ui.gui.normal.DefaultSettingsPage.DESC_SHADOW_COLOR;


public class CategoryComponent extends GuiEvents {
    public Setting<?> setting;

    public CategoryComponent(Setting<?> setting) {
        this.setting = setting;
    }

    public void render(Quad quad){
        HybridRenderText text = HybridTextRenderer.getTextRenderer(
                setting.getName(),
                FontStyle.BOLD,
                18,
                Color.WHITE,
                Color.WHITE,
                false
        );

        text.setPosition(quad.getX()+5 , quad.getY() + 6);

        HybridRenderText desc = HybridTextRenderer.getTextRenderer(
                "this is for testing and isnt a real desc",
                FontStyle.REGULAR,
                14,
                DESC_IDLE_COLOR,
                DESC_SHADOW_COLOR,
                false
        );

        desc.setPosition(quad.getX() + 5, quad.getY() + 20);

        HybridTextRenderer.addText(text);
        HybridTextRenderer.addText(desc);

    }
}
