package hybrid.api;

import hybrid.api.componenets.ToggleButtonComponent;
import hybrid.api.screen.HybridScreen;

public class TestScreen extends HybridScreen {
    public TestScreen() {
        super("test",300,180);

    }

    @Override
    public void registerComponents() {
        System.out.println("pre register");
        addComponent(new ToggleButtonComponent());
        super.registerComponents();
    }


}
