package hybrid.api.screen;

import hybrid.api.componenet.ComponentCategory;

import java.util.ArrayList;
import java.util.List;

public class ScreenCategoryBuilder {

    private final List<ComponentCategory> categories = new ArrayList<>();

    public static ScreenCategoryBuilder builder() {
        return new ScreenCategoryBuilder();
    }

    public ScreenCategoryBuilder addCategory(ComponentCategory category) {
        categories.add(category);
        return this;
    }


    public List<ComponentCategory> getCategories() {
        return categories;
    }
}
