package hybrid.api.mods.settings;


public class ModeSetting<E extends Enum<E>> extends ModSetting<E> {

    private final E[] values;

    public ModeSetting(String name, E defaultValue) {
        super(name, defaultValue);
        this.values = defaultValue.getDeclaringClass().getEnumConstants();
    }

    public E[] getValues() {
        return values;
    }


}
