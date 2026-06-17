package hybrid.api.mod;

import hybrid.api.mod.settings.BuiltCategory;

public class SprintMod extends HybridMod {

    public SprintMod() {
        super("Sprint", 1.0f);
    }

    @Override
    public void onInitialize() {

    }

    @Override
    public void onSetupSettings() {
        registerCategory(
                BuiltCategory.add("General")
                    .addBool("Enabled", true)
                    .addNumber("Speed", 1.2f, 1.0f, 2.0f)
        );
    }
}