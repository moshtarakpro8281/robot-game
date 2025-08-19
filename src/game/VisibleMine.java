package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class VisibleMine extends Mine {
    public VisibleMine(int x, int y) {
        super(x, y, ObstacleType.MINE);
    }

    @Override
    public void applyEffect(Robot robot) {
        if (robot.isAlive()) {
            robot.takeDamage(30); // آسیب 30٪ برای مین آشکار
            System.out.println("ربات در (" + super.getX() + ", " + super.getY() + ") با مین آشکار برخورد کرد و 30% سلامتش کم شد!");
        }
    }

    // متد برای رندر مین آشکار (دایره قرمز)
    public Circle getRenderShape(int cellSize) {
        Circle circle = new Circle(cellSize / 2.5);
        circle.setFill(Color.RED);
        circle.setStroke(Color.BLACK);
        return circle;
    }
}