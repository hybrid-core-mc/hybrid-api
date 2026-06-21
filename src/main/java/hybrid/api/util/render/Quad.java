package hybrid.api.util.render;

public class Quad {
    public int x, y, width, height;

    public Quad(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public Quad copy(){
        return new Quad(this.x,this.y,this.width,this.height);
    }


    public Quad setX(int x) {
        this.x = x;
        return this;
    }

    public Quad setY(int y) {
        this.y = y;
        return this;
    }

    public Quad setWidth(int width) {
        this.width = width;
        return this;
    }

    
    public Quad set(Quad source) {
        this.x = source.x;
        this.y = source.y;
        this.width = source.width;
        this.height = source.height;
        return this;
    }

    
    public void set(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public Quad setHeight(int height) {
        this.height = height;
        return this;
    }

    public Quad addX(int value) {
        this.x += value;
        return this;
    }

    public Quad addY(int value) {
        this.y += value;
        return this;
    }

    public Quad addWidth(int value) {
        this.width += value;
        return this;
    }

    public Quad addHeight(int value) {
        this.height += value;
        return this;
    }

    public Quad subtractX(int value) {
        this.x -= value;
        return this;
    }

    public Quad subtractY(int value) {
        this.y -= value;
        return this;
    }

    public Quad subtractWidth(int value) {
        this.width -= value;
        return this;
    }

    public Quad subtractHeight(int value) {
        this.height -= value;
        return this;
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