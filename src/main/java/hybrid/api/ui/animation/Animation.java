package hybrid.api.ui.animation;

import static hybrid.api.HybridApi.mc;

public abstract class Animation {

    float getDelta() {
        return mc.getRenderTickCounter().getDynamicDeltaTicks();
    }

    public void update() {

    }

    float lerp(float a, float b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return a + (b - a) * t;
    }
}
