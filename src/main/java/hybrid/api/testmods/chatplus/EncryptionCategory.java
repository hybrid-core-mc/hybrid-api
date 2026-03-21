package hybrid.api.testmods.chatplus;

import hybrid.api.mod.category.ModCategory;
import hybrid.api.mod.category.ModCategorySettingBuilder;
import hybrid.api.mod.settings.ModeSetting;

public class EncryptionCategory extends ModCategory {
    ModeSetting<EncryptionMode> mode;

    public EncryptionCategory() {
        super("Encryption");
    }

    @Override
    public void build(ModCategorySettingBuilder builder) {

        builder.add(mode);
    }


    private enum EncryptionMode {
        Host,
        Client
    }
}
