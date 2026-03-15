package hybrid.api.ui.components.screen;

import hybrid.api.font.FontStyle;
import hybrid.api.font.HybridRenderText;
import hybrid.api.font.HybridTextRenderer;
import hybrid.api.mods.category.ModSettingCategory;
import hybrid.api.mods.settings.*;
import hybrid.api.rendering.HybridRenderer;
import hybrid.api.rendering.ScreenBounds;
import hybrid.api.theme.HybridTheme;
import hybrid.api.theme.HybridThemeMap;
import hybrid.api.theme.ThemeColorKey;
import hybrid.api.ui.animation.AlphaAnimation;
import hybrid.api.ui.components.HybridComponent;
import hybrid.api.ui.components.settings.*;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;

import java.awt.*;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ModCategoryComponent extends HybridComponent {

    private final ModSettingCategory modSettingCategory;
    private final List<SettingComponent> modSettingComponents = new ArrayList<>();
    private final Map<HybridComponent, ModSetting<?>> visibilityMap = new IdentityHashMap<>();

    private boolean extended;

    private float animHeight;
    private float animVel;
    private float borderAnim;
    private long lastNs;

    public ModCategoryComponent(ModSettingCategory modSettingCategory) {
        this.modSettingCategory = modSettingCategory;
        this.extended = false;

        for (ModSetting<?> setting : modSettingCategory.settings()) {
            SettingComponent comp = getHybridComponent(setting);

            if (comp != null) {
                modSettingComponents.add(comp);
                visibilityMap.put(comp, setting);
            }
        }

        animHeight = getCollapsedHeight();
        animVel = 0f;
        borderAnim = 0f;
        lastNs = System.nanoTime();
    }

    private static SettingComponent getHybridComponent(ModSetting<?> setting) {
        SettingComponent comp = null;

        if (setting instanceof BooleanSetting)
            comp = new BooleanComponent((BooleanSetting) setting);
        if (setting instanceof NumberSetting)
            comp = new NumberComponent((NumberSetting) setting);
        if (setting instanceof ModeSetting<?>)
            comp = new ModeComponent((ModeSetting<?>) setting);
        if (setting instanceof ColorSetting)
            comp = new ColorComponent((ColorSetting) setting);
        if (setting instanceof TextListSetting)
            comp = new TextListComponent((TextListSetting) setting);
        assert comp instanceof SettingComponent;

        return comp;
    }

    private boolean isVisible(HybridComponent component) {
        ModSetting<?> setting = visibilityMap.get(component);
        return setting == null || setting.getVisibleSupplier().getAsBoolean();
    }

    private int getCollapsedHeight() {
        return 34;
    }

    private int getExpandedHeight() {
        int spacing = 4;
        int total = 0;
        int visible = 0;

        for (SettingComponent c : modSettingComponents) {
            if (!isVisible(c)) continue;
            total += c.getHeight();
            visible++;
        }

        if (visible > 0)
            total += spacing * (visible - 1);

        return getCollapsedHeight() + spacing + total + 15;
    }

    private static float clamp(float v, float b) {
        return v < (float) 0.0 ? (float) 0.0 : (Math.min(v, b));
    }

    private static float smoothDamp(float current, float target, float[] velRef, float smoothTime, float dt) {
        smoothTime = Math.max(0.0001f, smoothTime);
        float omega = 2f / smoothTime;
        float x = omega * dt;
        float exp = 1f / (1f + x + 0.48f * x * x + 0.235f * x * x * x);

        float change = current - target;
        float temp = (velRef[0] + omega * change) * dt;
        velRef[0] = (velRef[0] - omega * temp) * exp;

        float out = target + (change + temp) * exp;

        if ((target - current > 0f) == (out > target)) {
            out = target;
            velRef[0] = 0f;
        }

        return out;
    }

    @Override
    public void setupBounds() {
        componentBounds = outerBounds.copy();
        componentBounds.setHeight((int) animHeight);
        super.setupBounds();
    }

    public void render(HybridRenderer renderer, AlphaAnimation animation) {

        long now = System.nanoTime();
        float dt = (now - lastNs) / 1_000_000_000f;
        lastNs = now;
        dt = clamp(dt, 0.05f);

        float targetH = extended ? getExpandedHeight() : getCollapsedHeight();
        float[] v = new float[]{animVel};
        animHeight = smoothDamp(animHeight, targetH, v, 0.14f, dt);
        animVel = v[0];

        if (Math.abs(animHeight - targetH) < 0.5f) {
            animHeight = targetH;
            animVel = 0f;
        }

        float borderTarget = extended ? 1f : 0f;
        borderAnim += (borderTarget - borderAnim) * clamp(dt * 12f, 1f);

        componentBounds.setHeight((int) animHeight);

        renderer.drawQuad(
                componentBounds,
                HybridThemeMap.get(ThemeColorKey.modBackgroundColor)
        );

        float cx = componentBounds.getX() + componentBounds.getWidth() / 2f;
        float cy = componentBounds.getY() + componentBounds.getHeight() / 2f;

        float bw = componentBounds.getWidth() * borderAnim;
        float bh = componentBounds.getHeight() * borderAnim;

        ScreenBounds border = new ScreenBounds(
                (int) (cx - bw / 2f),
                (int) (cy - bh / 2f),
                (int) bw,
                (int) bh
        );

        if (borderAnim > 0.01f) {
            renderer.drawOutlineQuad(
                    border,
                    HybridThemeMap.get(ThemeColorKey.modBackgroundColor),
                    HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                    10,
                    1
            );
        }

        HybridRenderText title = HybridTextRenderer.getTextRenderer(
                modSettingCategory.name(),
                FontStyle.BOLD,
                25,
                animation.withAlpha(Color.WHITE),
                true
        );

        HybridRenderText toggleIcon = HybridTextRenderer.getIconRenderer(
                extended ? "up" : "down",
                animation.withAlpha(Color.WHITE),
                componentBounds.getY()
        );

        int headerCenterY = componentBounds.getY() + getCollapsedHeight() / 2;

        title.setPosition(
                componentBounds.getX() + HybridTheme.xPadding,
                headerCenterY - title.getHeight() / 2
        );

        toggleIcon.setPosition(
                componentBounds.getX() + componentBounds.getWidth()
                        - HybridTheme.xPadding - toggleIcon.getWidth(),
                headerCenterY - toggleIcon.getHeight() / 2
        );

        HybridTextRenderer.addText(title);
        HybridTextRenderer.addText(toggleIcon);

        if (animHeight <= getCollapsedHeight() + 1f)
            return;

        int spacing = 4;
        int innerPadding = HybridTheme.xPadding;

        int bgWidth = componentBounds.getWidth() - (HybridTheme.xPadding * 2);
        int bgX = componentBounds.getX() + HybridTheme.xPadding;
        int startY = componentBounds.getY() + getCollapsedHeight() + spacing;

        int maxContentHeight = (int) animHeight - getCollapsedHeight() - spacing - 15;
        if (maxContentHeight <= 0) return;

        ScreenBounds background = new ScreenBounds(
                bgX,
                startY,
                bgWidth,
                maxContentHeight
        );

        renderer.drawOutlineQuad(
                background,
                HybridThemeMap.get(ThemeColorKey.modsBackgroundColor),
                HybridThemeMap.get(ThemeColorKey.modButtonOutlineColor),
                10,
                1
        );

        int contentWidth = bgWidth - (innerPadding * 2);
        int contentX = bgX + innerPadding;
        int currentY = background.getY();
        int remaining = maxContentHeight;
        for (int i = 0; i < modSettingComponents.size(); i++) {

            SettingComponent component = modSettingComponents.get(i);
            if (!isVisible(component)) continue;


            int h = component.getHeight();

            if (component instanceof ColorComponent) {
                if (remaining < 40) break;
            } else {
                if (remaining < h) break;
            }
            component.outerBounds = new ScreenBounds(contentX, currentY, contentWidth, h);
            component.componentBounds = component.outerBounds.copy();

            component.renderPre(renderer);
            component.render(renderer);

            boolean hasNextVisible = false;
            for (int j = i + 1; j < modSettingComponents.size(); j++) {
                if (isVisible(modSettingComponents.get(j))) {
                    hasNextVisible = true;
                    break;
                }
            }

            if (hasNextVisible) {
                ScreenBounds line = component.outerBounds.copy();
                line.setSize(bgWidth, 1);
                line.setPosition(bgX, component.outerBounds.getY() + h);
                renderer.drawHorizontalLine(
                        line,
                        HybridThemeMap.get(ThemeColorKey.uiOutlineColor),
                        0.6f
                );
            }

            currentY += h + spacing;
            remaining -= h + spacing;
        }

    }

    @Override
    public void onMouseRelease(Click click) {

        if (componentBounds == null) return;

        ScreenBounds header = componentBounds.copy();
        header.setHeight(getCollapsedHeight());

        if (header.contains(click.x(), click.y())) {
            extended = !extended;
        }

        if (animHeight > getCollapsedHeight() + 1f) {
            modSettingComponents.forEach(c -> {
                if (isVisible(c)) c.onMouseRelease(click);
            });
        }

        super.onMouseRelease(click);
    }

    public int getTotalHeight() {
        return Math.max(getCollapsedHeight(), (int) Math.ceil(animHeight));
    }

    @Override
    public void onMouseClicked(Click click) {
        if (animHeight > getCollapsedHeight() + 1f) {
            modSettingComponents.forEach(c -> {
                if (isVisible(c)) c.onMouseClicked(click);
            });
        }
        super.onMouseClicked(click);
    }

    @Override
    public void onCharTyped(CharInput input) {
        modSettingComponents.forEach(settingComponent -> settingComponent.onCharTyped(input));
        super.onCharTyped(input);
    }

    @Override
    public void keyPressed(KeyInput input) {
        modSettingComponents.forEach(settingComponent -> settingComponent.keyPressed(input));
        super.keyPressed(input);
    }

    @Override
    public void onMouseScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        modSettingComponents.forEach(settingComponent -> settingComponent.onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount));
        super.onMouseScroll(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void onMouseDrag(Click click) {
        if (animHeight > getCollapsedHeight() + 1f) {
            modSettingComponents.forEach(c -> {
                if (isVisible(c)) c.onMouseDrag(click);
            });
        }
        super.onMouseDrag(click);
    }


}