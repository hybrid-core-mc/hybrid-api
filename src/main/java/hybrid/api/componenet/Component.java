package hybrid.api.componenet;

import hybrid.api.componenet.componenets.SliderComponent;
import hybrid.api.componenet.componenets.ToggleButtonComponent;
import hybrid.api.rendering.HybridRenderer2D;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.settings.Setting;

public abstract class Component {

    Setting setting;
    ScreenBounds bounds, outerBounds;

    public Component(Setting setting) {
        this.setting = setting;
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

    public Setting getSetting() {
        return setting;
    }

    public int getHeight() {
        if (this instanceof ToggleButtonComponent) {
            return 25;
        }
        if (this instanceof SliderComponent) {
            return 15;
        }
        return 0;
    }


}
