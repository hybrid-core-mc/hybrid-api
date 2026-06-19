package hybrid.api.mod;

import hybrid.api.mod.settings.BuiltCategory;

public class SprintMod extends HybridMod {

    public SprintMod() {
        super("Chat Emoji", "POES POESP MA SE BIG POES LOL thisi s the real mods lol monao bao  ",1.0f);
    }

    @Override
    public void onInitialize() {

    }

    @Override
    public void onSetupSettings() {
        registerCategory(
                BuiltCategory.add("General")
                    .addBool("Enabled", "weather toe anble the emoji function",true)
                    .addNumber("Speed", "speed of the chat LOL idk wtf",1.2f, 1.0f, 2.0f)
        );
        ;
    }
}