package hybrid.api.testmods.chatplus;

import com.google.common.eventbus.Subscribe;
import hybrid.api.mod.HybridMod;
import hybrid.api.mod.ModLink;
import hybrid.api.mod.category.ModSettingCategory;

import java.util.List;

public class ChatPlusMod extends HybridMod {
    public ChatPlusMod() {
        super("chat-plus", "Adds emojis to the minecraft chat its cool.\n TBh moaning bear is so maoning LOL xd", 0.f);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();
    }

    @Override
    protected List<ModSettingCategory> createSettings() {
        return List.of(
                registerCategory(new FilterCategory()),
                registerCategory(new InputCategory())
        );
    }

    @Override
    protected ModLink getGithub() {
        return new ModLink("SplashAni", "chat-plus");
    }

    @Override
    protected ModLink getModrinth() {
        return new ModLink("SplashPOOP", "chat-plus");
    }


}
