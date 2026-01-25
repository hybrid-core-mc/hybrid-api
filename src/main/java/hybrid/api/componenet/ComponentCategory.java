package hybrid.api.componenet;

import hybrid.api.componenet.componenets.ToggleButtonComponent;
import hybrid.api.settings.Setting;
import hybrid.api.settings.ToggleButtonSetting;

import java.util.ArrayList;
import java.util.List;

public class ComponentCategory {

    private final List<Component> components = new ArrayList<>();
    private final String categoryName;

    private ComponentCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public static Builder builder(String categoryName) {
        return new Builder(categoryName);
    }

    public List<Component> getComponents() {
        return components;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public static class Builder {

        private final ComponentCategory category;

        public Builder(String categoryName) {
            this.category = new ComponentCategory(categoryName);
        }

        public Builder addSetting(Setting setting) {
            if (setting instanceof ToggleButtonSetting toggle) {
                category.components.add(new ToggleButtonComponent(toggle));
            }
            return this;
        }

        public Builder registerComponent(Component component) {
            category.components.add(component);
            return this;
        }

        public ComponentCategory build() {
            return category;
        }
    }
}
