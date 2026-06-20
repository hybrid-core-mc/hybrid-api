package hybrid.api;

import hybrid.api.util.render.HueCirclePicker;
import hybrid.api.util.render.TriangleGradientPicker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;

public class ColorPickerScreen extends net.minecraft.client.gui.screens.Screen {

    TriangleGradientPicker triangleGradientPicker;
    HueCirclePicker picker;
    protected ColorPickerScreen() {
        super(net.minecraft.network.chat.Component.literal("Color Picker [Debug]"));
        triangleGradientPicker = new TriangleGradientPicker(50);
        picker = new HueCirclePicker(50);
    }



    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = 10;
        int y = 10;
        triangleGradientPicker.render(guiGraphics, mouseX, mouseY,x,y);
        picker.draw(x+50,y+50, Color.GREEN);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        triangleGradientPicker.mouseClicked(mouseButtonEvent);
        return super.mouseClicked(mouseButtonEvent, bl);
    }
    @Override
    public boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double d, double e) {
        picker.mouseClicked(mouseButtonEvent);
        return super.mouseDragged(mouseButtonEvent, d, e);
    }
}