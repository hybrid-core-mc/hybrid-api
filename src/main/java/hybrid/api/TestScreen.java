package hybrid.api;

import hybrid.api.componenet.ComponentCategory;
import hybrid.api.screen.HybridScreen;
import hybrid.api.screen.ScreenCategoryBuilder;
import hybrid.api.settings.ToggleButtonSetting;

public class TestScreen extends HybridScreen {
    public TestScreen() {
        super("test",300,180);
    }

    @Override
    public void registerCategories() {

        ScreenCategoryBuilder screenCategoryBuilder = ScreenCategoryBuilder.builder()
                .addCategory(
                        ComponentCategory.builder("Respawning")
                                .addSetting(new ToggleButtonSetting("Show killcam", true))
                                .addSetting(new ToggleButtonSetting("ismonogay", false))
                                .build()
                )
                .addCategory(
                        ComponentCategory.builder("LOLOL")
                                .addSetting(new ToggleButtonSetting("xd", false))
                                .build()
                );


        setBuilt(screenCategoryBuilder);

        super.registerCategories();
    }
}
