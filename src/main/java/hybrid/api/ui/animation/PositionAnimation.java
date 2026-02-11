package hybrid.api.ui.animation;

public class PositionAnimation extends Animation {

    private float current;
    private float target;
    private final float smoothing;

    public PositionAnimation(float startValue, float smoothing) {
        this.current = startValue;
        this.target = startValue;
        this.smoothing = smoothing;
    }

    public void setTarget(float target) {
        this.target = target;
    }


    @Override
    public void update() {

        float t = 1.0f - (float) Math.exp(-smoothing * getDelta());

        current += (target - current) * t;

        super.update();
    }

    public float get() {
        return current;
    }

    public void snap(float targetWidth) {
        this.current =targetWidth;
    }
}