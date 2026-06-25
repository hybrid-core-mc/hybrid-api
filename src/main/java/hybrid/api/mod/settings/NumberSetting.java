package hybrid.api.mod.settings;

public class NumberSetting extends Setting<Float> {

    private final float min;
    private final float max;

    public NumberSetting(String name,String desc, float value, float min, float max) {
        super(name,desc, value);
        this.min = min;
        this.max = max;
    }

    @Override
    public void set(Float value) {
        super.set(Math.max(min, Math.min(max, value)));
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public int getInt() {
        return Math.round(get());
    }
}