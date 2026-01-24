package hybrid.api;

import hybrid.api.componenets.ClickButtonComponent;
import hybrid.api.componenets.ComponentCategory;
import hybrid.api.componenets.ToggleButtonComponent;
import hybrid.api.screen.HybridScreen;

public class TestScreen extends HybridScreen {
    public TestScreen() {
        super("test",300,180);

    }

    @Override
    public void registerComponents() {

        System.out.println("pre register");
        ComponentCategory test = new ComponentCategory("Hey");

        test.addComponent(new ToggleButtonComponent());
        test.addComponent(new ClickButtonComponent());

        addComponent(test);

        ComponentCategory testing = new ComponentCategory("test");
        testing.addComponent(new ToggleButtonComponent());
        test.addComponent(new ClickButtonComponent());

        addComponent(test);

        super.registerComponents();
    }


}
