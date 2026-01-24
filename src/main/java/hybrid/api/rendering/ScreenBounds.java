package hybrid.api.rendering;

public class ScreenBounds {
    int x, y, width, height;

    public ScreenBounds() {
    }

    public ScreenBounds(int width, int height) {
        this.width = width;
        this.height = height;
    }

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

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
