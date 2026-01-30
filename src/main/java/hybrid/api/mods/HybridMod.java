package hybrid.api.mods;

public class HybridMod {
    String name;
    float version;

    public HybridMod(String name, float version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }
}
