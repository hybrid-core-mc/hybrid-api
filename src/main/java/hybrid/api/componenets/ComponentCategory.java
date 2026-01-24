package hybrid.api.componenets;


import java.util.ArrayList;
import java.util.List;

public class ComponentCategory {
     List<Component> components = new ArrayList<>();
    String categoryName;

    public ComponentCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public List<Component> getComponents() {
        return components;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
