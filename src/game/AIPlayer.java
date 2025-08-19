package game;

import java.awt.Point;
import java.util.List;

public class AIPlayer {
    private Robot robot;
    private AILevel aiLevel;
    private boolean isAIvsAI; // حالت بازی: true برای AI vs AI، false برای Player vs AI
    private String id;  // شناسه منحصر به فرد برای هر بازیکن

    // سطح هوش مصنوعی
    public enum AILevel {
        EASY, MEDIUM, HARD
    }

    // سازنده
    public AIPlayer(Robot robot, AILevel aiLevel, boolean isAIvsAI, String id) {
        this.robot = robot;
        this.aiLevel = aiLevel;
        this.isAIvsAI = isAIvsAI;
        this.id = id;  // مقداردهی شناسه
    }

    // متد برای دریافت شناسه بازیکن
    public String getId() {
        return id;
    }

    // متد برای دریافت موقعیت ربات
    public Point getPosition() {
        return new Point(robot.getX(), robot.getY());
    }

    // تصمیم‌گیری هوش مصنوعی
    public AIAction makeDecision(GameState gameState) {
        if (robot == null || !robot.isAlive() || robot.hasShot()) {
            System.out.println("Warning: Robot is null, dead, or has shot");
            return new AIAction(AIAction.ActionType.WAIT, null);
        }

        // بررسی محدودیت‌های نقشه
        Point robotPos = getPosition();
        if (robotPos.x < 0 || robotPos.x >= gameState.getMapWidth() ||
                robotPos.y < 0 || robotPos.y >= gameState.getMapHeight()) {
            System.out.println("Warning: Robot position out of map bounds");
            return new AIAction(AIAction.ActionType.WAIT, null);
        }

        // تصمیم‌گیری بر اساس حالت بازی
        return isAIvsAI ? makeAIDecision(gameState) : makePlayerVsAIDecision(gameState);
    }

    // تصمیم‌گیری برای حالت Player vs AI یا AI vs AI
    private AIAction makePlayerVsAIDecision(GameState gameState) {
        Point nearestEnemyPos = findNearestEnemy(gameState);
        if (nearestEnemyPos == null) {
            System.out.println("Warning: No valid enemy found");
            return new AIAction(AIAction.ActionType.WAIT, null);
        }

        Point robotPos = getPosition();
        double distance = robotPos.distance(nearestEnemyPos);

        // تنظیم جهت ربات به سمت دشمن
        Direction newDirection = getDirectionToPoint(nearestEnemyPos.x - robotPos.x, nearestEnemyPos.y - robotPos.y);
        robot.setDirection(newDirection);

        // تصمیم‌گیری بر اساس فاصله و سطح دشواری
        if (distance <= robot.getRange() && robot.getAmmo() > 0) {
            return new AIAction(AIAction.ActionType.ATTACK, new Point(nearestEnemyPos.x, nearestEnemyPos.y));
        } else if (isPathClear(robotPos, nearestEnemyPos, gameState)) {
            Point nextStep = getNextStep(robotPos, nearestEnemyPos, gameState);
            if (nextStep != null) {
                return new AIAction(AIAction.ActionType.MOVE, nextStep);
            }
        }

        // برای سطح HARD، مسیر جایگزین پیدا کن
        if (aiLevel == AILevel.HARD) {
            return findAlternativePath(gameState);
        }

        return new AIAction(AIAction.ActionType.WAIT, null);
    }

    // تصمیم‌گیری برای حالت AI vs AI
    private AIAction makeAIDecision(GameState gameState) {
        // استفاده از همان منطق Player vs AI برای AI vs AI، بدون رفتار تصادفی
        return makePlayerVsAIDecision(gameState);
    }

    // پیدا کردن نزدیک‌ترین دشمن
    private Point findNearestEnemy(GameState gameState) {
        Point robotPos = getPosition();
        List<Robot> enemyRobots = gameState.getEnemyRobots(this);
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

    // محاسبه جهت به سمت یک نقطه
    private Direction getDirectionToPoint(int dx, int dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else if (dy != 0) {
            return dy > 0 ? Direction.DOWN : Direction.UP;
        }
        return robot.getDirection(); // حفظ جهت فعلی اگر در مقصد هستیم
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

    // پیدا کردن مسیر جایگزین (برای HARD)
    private AIAction findAlternativePath(GameState gameState) {
        Direction[] directions = Direction.values();
        for (Direction dir : directions) {
            Point newPos = getNextStepForDirection(robot.getX(), robot.getY(), dir);
            if (newPos.x >= 0 && newPos.x < gameState.getMapWidth() &&
                    newPos.y >= 0 && newPos.y < gameState.getMapHeight() &&
                    gameState.getMap()[newPos.y][newPos.x].isWalkable()) {
                robot.setDirection(dir);
                return new AIAction(AIAction.ActionType.MOVE, newPos);
            }
        }
        return new AIAction(AIAction.ActionType.WAIT, null);
    }

    // گرفتن قدم بعدی بر اساس جهت
    private Point getNextStepForDirection(int x, int y, Direction direction) {
        switch (direction) {
            case UP:
                return new Point(x, y - 1);
            case DOWN:
                return new Point(x, y + 1);
            case LEFT:
                return new Point(x - 1, y);
            case RIGHT:
                return new Point(x + 1, y);
            default:
                return new Point(x, y);
        }
    }

    // متدهای getter و setter
    public Robot getRobot() {
        return robot;
    }

    public AILevel getAiLevel() {
        return aiLevel;
    }

    public void setGameMode(boolean isAIvsAI) {
        this.isAIvsAI = isAIvsAI;
    }

    public void takeDamage(int damage) {
        robot.takeDamage(damage);
    }

    public String getName() {
        return robot.getName();
    }

    // کلاس AIAction
    public static class AIAction {
        public enum ActionType {
            MOVE, ATTACK, WAIT
        }

        private ActionType type;
        private Point target; // تغییر از Object به Point برای هماهنگی

        public AIAction(ActionType type, Point target) {
            this.type = type;
            this.target = target;
        }

        public ActionType getType() {
            return type;
        }

        public Point getTarget() {
            return target;
        }
    }
}

