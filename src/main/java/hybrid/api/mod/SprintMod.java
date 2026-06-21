package hybrid.api.mod;

import hybrid.api.mod.settings.BuiltCategory;

import java.awt.*;

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
                             .addColor("color", "gay color picekr",Color.RED)

                             .addBool("Enabled", "weather toe anble the emoji function",true)
                    .addNumber("Speed", "speed of the chat LOL idk wtf",1.2f, 1.0f, 2.0f)
        );
        registerCategory(
                BuiltCategory.add("Gay boys")
                             .addBool("sigma", "weather toe anble the emoji function",true)
                             .addNumber("boy", "speed of the chat LOL idk wtf",1.2f, 1.0f, 2.0f)
                             .addMode("Gay level", "choose gay level", GayMode.MONOGAY)
        );
    }

    public enum GayMode {
        LITTLE,
        ALOT,
        EXTREMELY,
        MONOGAY
    }
}