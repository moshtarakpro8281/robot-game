package game;

import java.awt.Point;
import java.util.List;

public class AIAction {

    public enum ActionType {
        MOVE, ATTACK, WAIT
    }

    private ActionType type;
    private Point target; // هدف حرکت یا حمله

    // سازنده
    public AIAction(AIPlayer aiPlayer, GameState gameState) {
        if (aiPlayer == null || gameState == null || aiPlayer.getRobot() == null) {
            System.out.println("Warning: AIPlayer, GameState, or Robot is null");
            this.type = ActionType.WAIT;
            this.target = null;
            return;
        }

        // دریافت موقعیت ربات
        Point robotPos = new Point(aiPlayer.getRobot().getX(), aiPlayer.getRobot().getY());

        // پیدا کردن نزدیک‌ترین دشمن
        Point nearestEnemyPos = findNearestEnemy(aiPlayer, gameState);

        if (nearestEnemyPos == null) {
            System.out.println("Warning: No valid enemy found");
            this.type = ActionType.WAIT;
            this.target = null;
        } else {
            // محاسبه فاصله تا نزدیک‌ترین دشمن
            double distance = robotPos.distance(nearestEnemyPos);

            // تصمیم‌گیری بر اساس فاصله
            if (distance <= 5) { // محدوده حمله
                this.type = ActionType.ATTACK;
                this.target = new Point(nearestEnemyPos.x, nearestEnemyPos.y);
            } else if (isPathClear(robotPos, nearestEnemyPos, gameState)) { // بررسی مسیر باز
                this.type = ActionType.MOVE;
                this.target = new Point(nearestEnemyPos.x, nearestEnemyPos.y);
            } else {
                this.type = ActionType.WAIT;
                this.target = null;
            }
        }

        // لاگ برای دیباگ
        System.out.println("AI Action: " + type + ", Target: " + (target != null ? target : "None"));
    }

    // پیدا کردن نزدیک‌ترین دشمن
    private Point findNearestEnemy(AIPlayer aiPlayer, GameState gameState) {
        Point robotPos = new Point(aiPlayer.getRobot().getX(), aiPlayer.getRobot().getY());
        List<Robot> enemyRobots = gameState.getEnemyRobots(aiPlayer);
        Point nearestEnemyPos = null;
        double minDistance = Double.MAX_VALUE;

        for (Robot enemy : enemyRobots) {
            if (enemy.isAlive()) {
                Point enemyPos = new Point(enemy.getX(), enemy.getY());
                double distance = robotPos.distance(enemyPos);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestEnemyPos = enemyPos;
                }
            }
        }

        return nearestEnemyPos;
    }

    // بررسی مسیر باز
    private boolean isPathClear(Point start, Point end, GameState gameState) {
        Cell[][] map = gameState.getMap();
        int startX = start.x;
        int startY = start.y;
        int endX = end.x;
        int endY = end.y;

        // بررسی اینکه مقصد در محدوده نقشه باشد
        if (endX < 0 || endX >= gameState.getMapWidth() || endY < 0 || endY >= gameState.getMapHeight()) {
            return false;
        }

        // بررسی اینکه سلول مقصد قابل عبور باشد
        return map[endX][endY].isWalkable();
    }

    // متدهای getter
    public ActionType getType() {
        return type;
    }

    public Point getTarget() {
        return target;
    }
}