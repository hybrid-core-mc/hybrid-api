package hybrid.api.ui.animation;

import static hybrid.api.HybridApi.mc;

public abstract class Animation {

    float getDelta() {
        return mc.getRenderTickCounter().getDynamicDeltaTicks();
    }

    public void update() {

    }

    int lerp(int a, int b, float t) {
        return (int) (a + (b - a) * t);
    }
}
