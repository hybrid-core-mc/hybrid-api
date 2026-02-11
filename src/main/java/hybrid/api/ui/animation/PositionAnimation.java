package hybrid.api.ui.animation;

public class PositionAnimation extends Animation {

    private float current;
    private float target;
    private float goal;
    private final float smoothing;

    private boolean running = false;

    public PositionAnimation(float startValue, float smoothing) {
        this.current = startValue;
        this.target = startValue;
        this.goal = startValue;
        this.smoothing = smoothing;
    }

    public void setTarget(float target) {
        this.target = target;

        float dir = Math.signum(target - current);
        this.goal = target + dir * 3f;

        this.running = true;
    }

    @Override
    public void update() {
        if (!running) return;

        float dt = getDelta();
        if (dt <= 0f) return;

        float t = 1.0f - (float) Math.exp(-smoothing * dt);
        current += (goal - current) * t;

        if ((goal > target && current >= target) ||
                (goal < target && current <= target)) {

            current = target;
            running = false;
            return;
        }

        super.update();
    }

    public float get() {
        return current;
    }

    public void snap(float value) {
        current = value;
        target = value;
        goal = value;
        running = false;
    }
}