package hybrid.api.ui;

import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.shader.HueShader;
import hybrid.api.ui.components.HybridComponent;
import hybrid.api.ui.components.screen.ModHybridComponent;
import hybrid.api.ui.components.screen.ModsSidebarComponenet;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HybridScreen extends Screen {


    private final ScreenBounds bounds;
    private final List<HybridComponent> hybridModComponentList = new ArrayList<>();
    public HybridScreen(String name, int width, int height) {
        super(Text.of("hybrid.screen.".concat(name)));

        this.bounds = new ScreenBounds(0, 0, width, height);

        ModHybridComponent hybridComponent = new ModHybridComponent(); // mod content
        ModsSidebarComponenet sidebarComponent = new ModsSidebarComponenet(hybridComponent);

        hybridModComponentList.add(sidebarComponent);
        hybridModComponentList.add(hybridComponent);
    }

    public List<HybridComponent> getHybridModComponentList() {
        return hybridModComponentList;
    }

    public ScreenBounds getBounds() {
        return bounds;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {

        bounds.setCentered(context.getScaledWindowWidth(), context.getScaledWindowHeight());

        HybridRenderer renderer = HybridRenderer.RENDERER_INSTANCE;

        renderer.beginScissors(bounds);
        for (HybridComponent component : hybridModComponentList) {
            component.outerBounds = bounds;
            component.setupBounds();
            component.renderPre(renderer);
            component.render(renderer);
        }
        renderer.endScissors();

        clip(context);
        HueShader.drawHueRing(context, new ScreenBounds(mouseX, mouseY, 100, 100), Color.RED);
        context.fill(mouseX,mouseY,mouseX+100,mouseY+100,new Color(206, 206, 206,100).getRGB());
        context.disableScissor();

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        hybridModComponentList.forEach(component -> component.onMouseClicked(click));
        return super.mouseClicked(click, doubled);
    }


    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        hybridModComponentList.forEach(component -> component.onMouseDrag(click));
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {

        hybridModComponentList.forEach(component -> component.onMouseRelease(click));

        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

        hybridModComponentList.forEach(component -> component.onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount));

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(CharInput input) {
        hybridModComponentList.forEach(component -> component.onCharTyped(input));
        return super.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        hybridModComponentList.forEach(component -> component.keyPressed(input));
        return super.keyPressed(input);
    }

    public void clip(DrawContext context) {
        context.enableScissor(
                bounds.getX(),
                bounds.getY(),
                bounds.getX() + bounds.getWidth(),
                bounds.getY() + bounds.getHeight()
        );
    }
}
