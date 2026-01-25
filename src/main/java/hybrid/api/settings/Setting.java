package hybrid.api.settings;

public abstract class Setting {
    String name;

    public Setting(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
