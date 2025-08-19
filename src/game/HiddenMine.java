package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class HiddenMine extends Mine {
    public HiddenMine(int x, int y) {
        super(x, y, ObstacleType.MINE);
    }

    @Override
    public void applyEffect(Robot robot) {
        if (robot.isAlive()) {
            robot.takeDamage(50); // آسیب 50٪ برای مین مخفی
            System.out.println("ربات در (" + super.getX() + ", " + super.getY() + ") با مین مخفی برخورد کرد و 50% سلامتش کم شد!");
        }
    }

    // متد برای رندر مین مخفی (نقطه قرمز)
    public Rectangle getRenderShape(int cellSize) {
        Rectangle point = new Rectangle(cellSize / 4, cellSize / 4);
        point.setFill(Color.RED);
        point.setStroke(Color.BLACK);
        return point;
    }
}