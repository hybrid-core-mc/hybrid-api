package hybrid.api.util.render;

public class Quad {
    public int x, y, width, height;

    public Quad(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public Quad copy(Quad quad) {
        return new Quad(quad.x, quad.y, quad.width, quad.height);
    }

    public void expand(int amountX, int amountY) {
        this.x -= amountX;
        this.y -= amountY;
        this.width += amountX * 2;
        this.height += amountY * 2;
    }

    public void shrink(int amountX, int amountY) {
        this.x += amountX;
        this.y += amountY;
        this.width -= amountX * 2;
        this.height -= amountY * 2;
    }

    public void expandX(int amount) {
        this.x -= amount;
        this.width += amount * 2;
    }

    public void shrinkX(int amount) {
        this.x += amount;
        this.width -= amount * 2;
    }

    public void expandY(int amount) {
        this.y -= amount;
        this.height += amount * 2;
    }

    public void shrinkY(int amount) {
        this.y += amount;
        this.height -= amount * 2;
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
}