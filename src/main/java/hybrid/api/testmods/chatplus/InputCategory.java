package hybrid.api.testmods.chatplus;

import com.google.common.eventbus.Subscribe;
import hybrid.api.event.ScreenRenderEvent;
import hybrid.api.mod.category.ModCategory;
import hybrid.api.mod.category.ModCategorySettingBuilder;
import hybrid.api.mod.settings.BooleanSetting;
import net.minecraft.client.gui.screen.ChatScreen;

public class InputCategory extends ModCategory {

    BooleanSetting keyboard = new BooleanSetting("Screen Keyboard", false);

    public InputCategory() {
        super("Streamer");
    }

    @Override
    public void build(ModCategorySettingBuilder builder) {

    }

    @Subscribe
    public void onrender(ScreenRenderEvent event) {
        if (!(event.screen instanceof ChatScreen)) return;
        event.context.fill(5, 5, 100, 100, -1);
    }
}
