    package hybrid.api.ui.animation;

    import java.awt.*;

    public class AlphaAnimation extends Animation {

        private float current;
        private float target;
        private float smoothing;

        public AlphaAnimation(float start, float smoothing) {
            this.current = start;
            this.target = start;
            this.smoothing = smoothing;
        }

        public Color withAlpha(Color c) {
            float a = Math.max(0f, Math.min(1f, current));
            return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(a * 255));
        }
        public void setTarget(float target) {
            this.target = target;
        }
        public void update() {

            float t = 1.0f - (float) Math.exp(-smoothing * getDelta());

            t = t * t * (3f - 2f * t);

            current = lerp(current, target, t);

            super.update();
        }

        public float get() {
            return current;
        }

        public void snap(float value) {
            current = value;
            target = value;
        }

        public void setSmoothing(float smoothing) {
            this.smoothing = smoothing;
        }
    }