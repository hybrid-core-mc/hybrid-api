package hybrid.api.ui.components.settings;

import hybrid.api.ui.components.HybridComponent;

public class SettingComponent extends HybridComponent {
    int height;

    public SettingComponent() {
        this.height = 30;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

}
