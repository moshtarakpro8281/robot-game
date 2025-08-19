package game;

import java.awt.Point;
import java.util.List;

public class AIEngine {

    // متد محاسبه بهترین اقدام
    public AIPlayer.AIAction calculateBestAction(AIPlayer aiPlayer, GameState gameState) {
        if (aiPlayer == null || gameState == null || aiPlayer.getRobot() == null) {
            System.out.println("Warning: AIPlayer, GameState, or Robot is null");
            return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.WAIT, null);
        }

        // استفاده از متد makeDecision در AIPlayer
        return aiPlayer.makeDecision(gameState);
    }

    // تصمیم‌گیری برای حالت AI vs AI یا Player vs AI
    public AIPlayer.AIAction makeDecision(AIPlayer aiPlayer, GameState gameState) {
        if (aiPlayer == null || gameState == null || aiPlayer.getRobot() == null) {
            System.out.println("Warning: AIPlayer, GameState, or Robot is null");
            return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.WAIT, null);
        }

        // استفاده از منطق AIPlayer برای تصمیم‌گیری
        return aiPlayer.makeDecision(gameState);
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

    // گرفتن قدم بعدی برای حرکت به سمت هدف
    private Point getNextStep(Point current, Point target, GameState gameState) {
        int dx = target.x - current.x;
        int dy = target.y - current.y;
        int nextX = current.x;
        int nextY = current.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            nextX = current.x + (dx > 0 ? 1 : -1);
        } else if (dy != 0) {
            nextY = current.y + (dy > 0 ? 1 : -1);
        } else {
            return null; // اگر در مقصد هستیم
        }

        // بررسی اینکه قدم بعدی در محدوده نقشه و قابل عبور باشد
        if (nextX >= 0 && nextX < gameState.getMapWidth() &&
                nextY >= 0 && nextY < gameState.getMapHeight() &&
                gameState.getMap()[nextY][nextX].isWalkable()) {
            return new Point(nextX, nextY);
        }

        return null;
    }

    // بررسی مسیر بدون مانع
    private boolean isPathClear(Point start, Point end, GameState gameState) {
        Cell[][] map = gameState.getMap();
        int endX = end.x;
        int endY = end.y;

        // بررسی اینکه مقصد در محدوده نقشه باشد
        if (endX < 0 || endX >= gameState.getMapWidth() || endY < 0 || endY >= gameState.getMapHeight()) {
            return false;
        }

        // بررسی اینکه سلول مقصد قابل عبور باشد
        return map[endY][endX].isWalkable();
    }
}
