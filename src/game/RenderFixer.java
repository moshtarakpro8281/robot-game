package game;

import javafx.application.Platform;
import javafx.scene.layout.GridPane;

public class RenderFixer {/*
    private boolean gameStarted = false;

    // اجرای رندر نقشه از طریق GameController
    public void render(GameController controller) {
        if (controller.getMapGrid() == null) {
            System.out.println("mapGrid is null, cannot render.");
            return;
        }

        Platform.runLater(() -> {
            try {
                // پاک کردن محتویات قبلی
                controller.getMapGrid().getChildren().clear();
                // رندر نقشه جدید (بدون رندر ربات‌ها)
                controller.renderMap();  // رندر نقشه جدید
                System.out.println("نقشه رندر شد. تعداد عناصر: " + controller.getMapGrid().getChildren().size());
            } catch (Exception e) {
                System.err.println("خطا در رندر: " + e.getMessage());
                e.printStackTrace();  // چاپ جزئیات خطا برای رفع مشکلات
            }
        });
    }

    // حرکت ربات
    public void moveRobot(GameController controller, int deltaX, int deltaY) {
        if (!gameStarted) {
            System.out.println("بازی هنوز شروع نشده! دکمه شروع رو بزن.");
            return;
        }

        Robot robot = controller.getCurrentRobot();
        if (robot != null && robot.isAlive()) {
            int newX = robot.getX() + deltaX;
            int newY = robot.getY() + deltaY;

            if (newX >= 0 && newX < controller.getBoardWidth() && newY >= 0 && newY < controller.getBoardHeight()) {
                if (controller.getMap()[newX][newY].isEmpty()) {
                    controller.getMap()[robot.getRow()][robot.getCol()].removeEntity();
                    robot.setPosition(newX, newY);
                    controller.getMap()[newX][newY].setEntity(robot);
                    controller.handleCollision(robot, newX, newY);
                    // دیگر نیازی به پاک کردن یا رندر مجدد نقشه نیست
                    // controller.getMapGrid().getChildren().clear();
                    // controller.renderMap();
                    controller.updateUI();  // بروزرسانی UI
                    System.out.println("ربات حرکت کرد به (" + newX + ", " + newY + ")");
                } else {
                    System.out.println("حرکت غیرمجاز یا مکان اشغال شده");
                }
            } else {
                System.out.println("موقعیت خارج از نقشه است!");
            }
        } else {
            System.out.println("ربات مرده یا ناموجود است!");
        }
    }

    // شروع بازی
    public void startGame() {
        gameStarted = true;
        System.out.println("بازی شروع شد!");
    }

    // توقف بازی
    public void pauseGame() {
        gameStarted = false;
        System.out.println("بازی متوقف شد!");
    }*/
}
