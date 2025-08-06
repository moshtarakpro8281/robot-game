package game;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerId;
    private boolean isHuman;
    private List<Robot> robots;

    public Player(String playerId, boolean isHuman) {
        this.playerId = playerId;
        this.isHuman = isHuman;
        this.robots = new ArrayList<>();
    }

    // افزودن یک ربات به لیست
    public void addRobot(Robot robot) {
        if (robots.size() < 2) {
            robots.add(robot);
        } else {
            System.out.println("❌ هر بازیکن فقط می‌تونه دو ربات داشته باشه!");
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isHuman() {
        return isHuman;
    }

    // متد جدید برای تشخیص AI بودن
    public boolean isAI() {
        return !isHuman;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public void printRobotStatus() {
        for (Robot robot : robots) {
            System.out.println("🔍 ربات " + robot.getName() + " | سلامت: " + robot.getHealth() + " | مهمات: " + robot.getAmmo());
        }
    }
}
