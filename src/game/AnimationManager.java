package game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.List;

public class AnimationManager {

    private final GridPane mapGrid;
    private final List<Robot> enemyRobots;
    private final int gridSize = 13; // مطابق با GRID_SIZE در GameController

    public AnimationManager(GridPane mapGrid, List<Robot> enemyRobots) {
        this.mapGrid = mapGrid;
        this.enemyRobots = enemyRobots;
    }

    // متد تبدیل Robot.Direction به AnimationManager.Direction
    public static Direction convertDirection(Robot.Direction robotDir) {
        return switch (robotDir) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> Direction.LEFT;
            case RIGHT -> Direction.RIGHT;
        };
    }

    public void playBulletAnimation(int startRow, int startCol, Direction direction) {
        Circle bullet = new Circle(10, Color.BLACK);
        mapGrid.add(bullet, startCol, startRow);

        final int[] currentRow = {startRow};
        final int[] currentCol = {startCol};

        final Timeline[] timeline = new Timeline[1];

        timeline[0] = new Timeline(new KeyFrame(Duration.millis(150), e -> {
            // حذف گلوله از موقعیت قبلی
            mapGrid.getChildren().remove(bullet);

            // حرکت گلوله به سمت جهت مورد نظر
            switch (direction) {
                case UP -> currentRow[0]--;
                case DOWN -> currentRow[0]++;
                case LEFT -> currentCol[0]--;
                case RIGHT -> currentCol[0]++;
            }

            // بررسی برخورد با مرزهای شبکه
            if (currentRow[0] < 0 || currentRow[0] >= gridSize || currentCol[0] < 0 || currentCol[0] >= gridSize) {
                timeline[0].stop();
                mapGrid.getChildren().remove(bullet);
                return;
            }

            // بررسی برخورد با ربات‌ها
            for (Robot enemy : enemyRobots) {
                if (!enemy.isDestroyed() && enemy.getRow() == currentRow[0] && enemy.getCol() == currentCol[0]) {
                    enemy.destroy();
                    timeline[0].stop();
                    removeRobotShape(enemy);
                    mapGrid.getChildren().remove(bullet);
                    return;
                }
            }

            // اضافه کردن گلوله به موقعیت جدید
            mapGrid.add(bullet, currentCol[0], currentRow[0]);
        }));

        timeline[0].setCycleCount(Timeline.INDEFINITE);
        timeline[0].play();
    }

    private void removeRobotShape(Robot robot) {
        mapGrid.getChildren().removeIf(node -> {
            Integer colIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);
            return colIndex != null && rowIndex != null
                    && colIndex == robot.getCol()
                    && rowIndex == robot.getRow();
        });
    }

    // تعریف enum جهت حرکت گلوله
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}


