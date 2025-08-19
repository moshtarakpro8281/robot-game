package game;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private List<AIPlayer> aiPlayers;  // لیست بازیکنان هوش مصنوعی
    private Cell[][] map;  // نقشه بازی
    private int mapWidth;  // عرض نقشه
    private int mapHeight;  // ارتفاع نقشه

    // سازنده برای مقداردهی اولیه
    public GameState(List<AIPlayer> aiPlayers, Cell[][] map, int mapWidth, int mapHeight) {
        this.aiPlayers = aiPlayers;
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    // متد برای پیدا کردن شناسه بازیکن از روی شیء AIPlayer
    public String getIdByPlayer(AIPlayer player) {
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer != null && aiPlayer.equals(player)) {
                return aiPlayer.getId();  // فرض بر این است که هر AIPlayer دارای شناسه است
            }
        }
        return null;  // اگر بازیکن پیدا نشد
    }

    // متد برای دریافت دشمنان یک بازیکن بر اساس شناسه
    public List<AIPlayer> getEnemies(String myId) {
        List<AIPlayer> enemies = new ArrayList<>();
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer != null && !aiPlayer.getId().equals(myId)) {
                enemies.add(aiPlayer);  // همه بازیکنانی که شناسه‌شان متفاوت است دشمن هستند
            }
        }
        return enemies;
    }

    // متد برای دریافت موقعیت یک بازیکن با استفاده از شیء AIPlayer
    public Point getPlayerPosition(AIPlayer player) {
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer != null && aiPlayer.equals(player)) {
                return aiPlayer.getPosition();  // دریافت موقعیت از متد getPosition() در AIPlayer
            }
        }
        return null;  // اگر بازیکن پیدا نشد
    }

    // متد جدید برای دریافت موقعیت همه ربات‌ها (موقعیت همه بازیکنان)
    public List<Point> getAllPlayerPositions() {
        List<Point> playerPositions = new ArrayList<>();
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer != null && aiPlayer.getRobot() != null) {
                playerPositions.add(aiPlayer.getPosition());  // دریافت موقعیت از متد getPosition()
            }
        }
        return playerPositions;
    }

    // متد برای دریافت ربات‌های زنده
    public List<Robot> getAliveRobots() {
        List<Robot> aliveRobots = new ArrayList<>();
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer != null && aiPlayer.getRobot().isAlive()) {
                aliveRobots.add(aiPlayer.getRobot());
            }
        }
        return aliveRobots;
    }

    // متد برای دریافت ربات‌های دشمن
    public List<Robot> getEnemyRobots(AIPlayer aiPlayer) {
        List<Robot> enemyRobots = new ArrayList<>();
        String myId = aiPlayer.getId();
        List<AIPlayer> enemies = getEnemies(myId);

        for (AIPlayer enemy : enemies) {
            if (enemy.getRobot().isAlive()) {
                enemyRobots.add(enemy.getRobot());
            }
        }
        return enemyRobots;
    }

    // متد برای دریافت تمامی ربات‌ها
    public List<Robot> getAllRobots() {
        List<Robot> allRobots = new ArrayList<>();
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer != null && aiPlayer.getRobot() != null) {
                allRobots.add(aiPlayer.getRobot());
            }
        }
        return allRobots;
    }

    // متد برای بررسی اینکه آیا رباتی به هدفش رسیده است
    public boolean isRobotAtTarget(Robot robot, Point target) {
        return robot.getX() == target.x && robot.getY() == target.y;
    }

    // سایر متدهای گتتر و ستتر
    public List<AIPlayer> getAiPlayers() {
        return aiPlayers;
    }

    public Cell[][] getMap() {
        return map;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    // متد برای ساخت GameState از وضعیت فعلی بازی
    public static GameState fromCurrentState(List<AIPlayer> aiPlayers, Cell[][] map, int mapWidth, int mapHeight) {
        return new GameState(aiPlayers, map, mapWidth, mapHeight);
    }
}



