package hybrid.api.rendering;

public class ScreenBounds {
    int x, y, width, height;


    public ScreenBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setCentered(int width, int height) {
        this.x = (width - getWidth()) / 2;
        this.y = (height - getHeight()) / 2;
    }

    public ScreenBounds copy() {
        return new ScreenBounds(x, y, width, height);
    }


    public void setWidth(int width) {
        this.width = width;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "x " + x + " y " + y + " width: " + width + " height " + height;
    }

    public boolean contains(double mouseX, double mouseY) {
        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }

}
