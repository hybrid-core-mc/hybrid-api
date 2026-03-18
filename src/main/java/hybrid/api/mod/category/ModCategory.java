package hybrid.api.mod.category;

public abstract class ModCategory {

    protected final String name;

    public ModCategory(String name) {
        this.name = name;
    }

    public abstract void build(ModCategorySettingBuilder builder);

    public ModSettingCategory toCategory() {
        ModCategorySettingBuilder builder = new ModCategorySettingBuilder(name);
        build(builder);
        return builder.build();
    }

}