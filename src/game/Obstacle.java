package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Obstacle extends Entity {
    public enum ObstacleType {
        MINE("Mine"), STEEL_WALL("Steel Wall"), NORMAL_WALL("Normal Wall"), WOODEN_WALL("Wooden Wall");

        private final String name;

        // سازنده برای نوع مانع
        ObstacleType(String name) {
            this.name = name;
        }

        // متد برای دریافت نام نوع مانع
        public String getName() {
            return name;
        }
    }

    private ObstacleType type; // نوع مانع

    // سازنده برای تعیین نوع مانع و موقعیت آن
    public Obstacle(int x, int y, ObstacleType type) {
        super(x, y);  // استفاده از سازنده کلاس پدر (Entity)
        this.type = type;
    }

    // متد برای دریافت نوع مانع
    public ObstacleType getType() {
        return type;
    }

    // متد برای دریافت نام مانع
    public String getName() {
        return type.getName();  // بازگشت نام نوع مانع
    }

    // متد انتزاعی برای اعمال اثرات مانع بر ربات
    public abstract void applyEffect(Robot robot);

    // متد برای نمایش گرافیکی مانع (یک مستطیل به عنوان نمای گرافیکی مانع)
    public Rectangle getView() {
        Rectangle rectangle = new Rectangle(30, 30);
        switch (type) {
            case MINE:
                rectangle.setFill(Color.RED);  // ماین به رنگ قرمز
                break;
            case STEEL_WALL:
                rectangle.setFill(Color.GRAY);  // دیوار فولادی به رنگ خاکستری
                break;
            case NORMAL_WALL:
                rectangle.setFill(Color.DARKGRAY);  // دیوار عادی به رنگ تیره
                break;
            case WOODEN_WALL:
                rectangle.setFill(Color.BROWN);  // دیوار چوبی به رنگ قهوه‌ای
                break;
        }
        return rectangle;
    }
}

