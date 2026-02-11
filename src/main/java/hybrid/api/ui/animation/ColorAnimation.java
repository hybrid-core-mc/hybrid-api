package hybrid.api.ui.animation;

import java.awt.*;

public class ColorAnimation extends Animation {

    private Color current;
    private Color target;
    private float smoothing;

    public ColorAnimation(Color start, float smoothing) {
        this.current = start;
        this.target = start;
        this.smoothing = smoothing;
    }

    public void setTarget(Color target) {
        this.target = target;
    }

    public void update() {

        float t = 1.0f - (float) Math.exp(-smoothing * getDelta());

        current = new Color(
                lerp(current.getRed(), target.getRed(), t),
                lerp(current.getGreen(), target.getGreen(), t),
                lerp(current.getBlue(), target.getBlue(), t),
                lerp(current.getAlpha(), target.getAlpha(), t)
        );

        super.update();
    }

    public Color get() {
        return current;
    }

    public void snap(Color color) {
        current = color;
        target = color;
    }

    public void setSmoothing(float smoothing) {
        this.smoothing = smoothing;
    }
}