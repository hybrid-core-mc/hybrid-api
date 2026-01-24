package hybrid.api.componenets;

import hybrid.api.rendering.HybridRenderer2D;
import hybrid.api.rendering.ScreenBounds;

public abstract class Component {

    private final String name;
    ScreenBounds bounds, outerBounds;

    public Component(String name) {
        this.name = name;
        bounds = new ScreenBounds(0, getHeight());
    }

    public void onUpdate() {
    }

    public ScreenBounds getBounds() {
        return bounds;
    }

    public void setOuterBounds(ScreenBounds outerBounds) {
        this.outerBounds = outerBounds;
    }

    public abstract void render(HybridRenderer2D renderer);

    public String getName() {
        return name;
    }


    public int getHeight() {
        if (this instanceof ToggleButtonComponent) {
            return 25;
        }
        if (this instanceof ClickButtonComponent) {
            return 15;
        }
        return 0;
    }

}
