package hybrid.api.testmods.chatplus;

import com.google.common.eventbus.Subscribe;
import hybrid.api.event.TickEvent;
import hybrid.api.mod.category.ModCategory;
import hybrid.api.mod.category.ModCategorySettingBuilder;
import hybrid.api.mod.settings.TextListSetting;

import java.util.List;

public class FilterCategory extends ModCategory {

    private TextListSetting words;

    public FilterCategory() {
        super("Filter");
    }

    @Override
    public void build(ModCategorySettingBuilder builder) {
        words = new TextListSetting("Words", List.of("Cuss", "World"));
        builder.add(words);
    }

    @Subscribe
    public void tick(TickEvent event){
        System.out.println(words.get());
    }

}