package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Obstacle extends Entity {
    public enum ObstacleType {
        MINE, STEEL_WALL, WOODEN_WALL, INDESTRUCTIBLE_WALL
    }

    private ObstacleType type;
    protected Rectangle view;
    private boolean destroyed = false;  // اضافه شده: وضعیت تخریب

    public Obstacle(int x, int y, ObstacleType type) {
        super(x, y);
        this.type = type;

        view = new Rectangle(30, 30);
        view.setX(x * 30);
        view.setY(y * 30);

        switch (type) {
            case MINE:
                view.setFill(Color.RED);
                break;
            case WOODEN_WALL:
                view.setFill(Color.SANDYBROWN);
                break;
            case STEEL_WALL:
                view.setFill(Color.DARKGRAY);
                break;
            case INDESTRUCTIBLE_WALL:
                view.setFill(Color.BLACK);
                break;
        }

        view.setStroke(Color.BLACK);
    }

    public ObstacleType getType() {
        return type;
    }

    public Rectangle getView() {
        return view;
    }

    // متد جدید برای وضعیت تخریب
    public boolean isDestroyed() {
        return destroyed;
    }

    // متد برای تنظیم وضعیت تخریب
    public void destroy() {
        this.destroyed = true;
        // مخفی کردن نمای گرافیکی در صورت نیاز
        view.setVisible(false);
    }

    // متد انتزاعی که باید در زیرکلاس‌ها پیاده شود
    public abstract void applyEffect(Robot robot);
}
