package hybrid.api.testmods.chatplus;

import com.google.common.eventbus.Subscribe;
import hybrid.api.event.ChatEvent;
import hybrid.api.mod.category.ModCategory;
import hybrid.api.mod.category.ModCategorySettingBuilder;
import hybrid.api.mod.settings.ModeSetting;
import hybrid.api.mod.settings.TextListSetting;

import java.util.List;

public class FilterCategory extends ModCategory {

    TextListSetting words;
    ModeSetting<ReplaceMode> replaceMode;

    public FilterCategory() {
        super("Filter");
    }

    @Override
    public void build(ModCategorySettingBuilder builder) {
        words = new TextListSetting("Words", List.of("Poop", "Flip"));
        replaceMode = (ModeSetting<ReplaceMode>) new ModeSetting<>("Replace Mode", ReplaceMode.Aestriks).visible(() -> !words.get().isEmpty());
        builder.add(words, replaceMode);
    }
    @Subscribe
    public void onChat(ChatEvent event) {

        String msg = event.getMessage();
        List<String> filterWords = words.get();

        String filtered = msg;

        for (String word : filterWords) {
            if (word == null || word.isEmpty()) continue;

            String replacement;

            switch (replaceMode.get()) {
                case Aestriks -> replacement = "*".repeat(word.length());

                case Remove -> replacement = "";

                case Random -> {
                    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < word.length(); i++) {
                        int index = (int) (Math.random() * chars.length());
                        sb.append(chars.charAt(index));
                    }

                    replacement = sb.toString();
                }

                default -> replacement = word;
            }

            filtered = filtered.replaceAll("(?i)" + java.util.regex.Pattern.quote(word), replacement);
        }

        if (!filtered.equals(msg)) {
            event.setOverride(filtered);
        }
    }

    private enum ReplaceMode {
        Aestriks, Random, Remove
    }
}