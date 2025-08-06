package game;

import javafx.scene.paint.Color;

public class IndestructibleWall extends Obstacle {

    public IndestructibleWall(int x, int y) {
        super(x, y, ObstacleType.INDESTRUCTIBLE_WALL);
        // تغییر رنگ نمای دیوار به دلخواه اینجا انجام می‌شود
        getView().setFill(Color.DARKGRAY);
        getView().setStrokeWidth(2);
    }

    @Override
    public void applyEffect(Robot robot) {
        // فقط مانع حرکت ربات است، تاثیری روی ربات ندارد
        System.out.println("ربات به دیوار غیرقابل تخریب در (" + getRow() + ", " + getCol() + ") برخورد کرد!");
    }

    @Override
    public void update() {
        // نیازی به بروزرسانی ندارد
    }

    // متد getView نیازی به override ندارد و از کلاس پایه استفاده می‌شود
}


