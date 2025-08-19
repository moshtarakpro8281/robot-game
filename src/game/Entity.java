package game;

import javafx.scene.shape.Shape;

public abstract class Entity {
    private int x;  // موقعیت افقی
    private int y;  // موقعیت عمودی

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Entity{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public abstract Shape getView();
}
