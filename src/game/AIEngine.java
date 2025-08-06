package game;

import java.awt.Point;
import java.util.*;

public class AIEngine {
    public enum AILevel {
        EASY, MEDIUM, HARD
    }

    private AILevel level;
    private Random random;

    public AIEngine(AILevel level) {
        this.level = level;
        this.random = new Random();
    }

    public AIPlayer.AIAction calculateBestAction(AIPlayer aiPlayer, GameState gameState) {
        String myId = gameState.getIdByPlayer(aiPlayer);
        if (myId == null) return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.WAIT, null);

        switch (level) {
            case EASY:
                return simpleAI(aiPlayer, gameState, myId);
            case MEDIUM:
                return intermediateAI(aiPlayer, gameState, myId);
            case HARD:
                return advancedAI(aiPlayer, gameState, myId);
            default:
                return simpleAI(aiPlayer, gameState, myId);
        }
    }

    private AIPlayer.AIAction simpleAI(AIPlayer aiPlayer, GameState gameState, String myId) {
        List<AIPlayer> enemies = gameState.getEnemies(myId);
        if (enemies.isEmpty()) return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.WAIT, null);

        AIPlayer nearest = findNearestEnemy(aiPlayer, enemies);
        if (aiPlayer.isInRange(nearest.getRobot()))
            return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.ATTACK, nearest.getRobot());

        Point move = calculateMoveTowards(aiPlayer.getPosition(), nearest.getPosition());
        return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.MOVE, move);
    }

    private AIPlayer.AIAction intermediateAI(AIPlayer aiPlayer, GameState gameState, String myId) {
        List<AIPlayer> enemies = gameState.getEnemies(myId);
        if (enemies.isEmpty()) return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.WAIT, null);

        double healthRatio = (double) aiPlayer.getHealth() / aiPlayer.getMaxHealth();
        if (healthRatio < 0.3) {
            Point retreat = findRetreatPosition(aiPlayer, enemies, gameState);
            return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.MOVE, retreat);
        }

        AIPlayer weakest = findWeakestEnemy(enemies);
        if (aiPlayer.isInRange(weakest.getRobot()))
            return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.ATTACK, weakest.getRobot());

        Point move = calculateMoveTowards(aiPlayer.getPosition(), weakest.getPosition());
        return new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.MOVE, move);
    }

    private AIPlayer.AIAction advancedAI(AIPlayer aiPlayer, GameState gameState, String myId) {
        return minimaxDecision(aiPlayer, gameState, 3, myId);
    }

    private AIPlayer.AIAction minimaxDecision(AIPlayer aiPlayer, GameState gameState, int depth, String myId) {
        List<AIPlayer.AIAction> actions = generatePossibleActions(aiPlayer, gameState, myId);
        AIPlayer.AIAction best = null;
        int bestScore = Integer.MIN_VALUE;

        for (AIPlayer.AIAction action : actions) {
            GameState newState = simulateAction(gameState, aiPlayer, action);
            int score = minimaxValue(newState, depth - 1, false, aiPlayer);
            if (score > bestScore) {
                bestScore = score;
                best = action;
            }
        }

        return best != null ? best : new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.WAIT, null);
    }

    private int minimaxValue(GameState state, int depth, boolean isMax, AIPlayer origin) {
        if (depth == 0 || state.isGameOver())
            return evaluateGameState(state, origin);

        if (isMax) {
            int max = Integer.MIN_VALUE;
            for (AIPlayer.AIAction a : generatePossibleActions(origin, state, state.getIdByPlayer(origin))) {
                GameState s = simulateAction(state, origin, a);
                max = Math.max(max, minimaxValue(s, depth - 1, false, origin));
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            String originId = state.getIdByPlayer(origin);
            if (originId == null) return 0;

            for (AIPlayer enemy : state.getEnemies(originId)) {
                for (AIPlayer.AIAction a : generatePossibleActions(enemy, state, state.getIdByPlayer(enemy))) {
                    GameState s = simulateAction(state, enemy, a);
                    min = Math.min(min, minimaxValue(s, depth - 1, true, origin));
                }
            }
            return min;
        }
    }

    private int evaluateGameState(GameState state, AIPlayer player) {
        int score = player.getHealth() * 2;

        String playerId = state.getIdByPlayer(player);
        if (playerId == null) return score;

        for (AIPlayer enemy : state.getEnemies(playerId)) {
            double d = calculateDistance(player.getPosition(), enemy.getPosition());
            if (d <= player.getRange()) score += 50;
            if (d < 2 && enemy.getHealth() > player.getHealth()) score -= 30;
        }

        return score;
    }

    private List<AIPlayer.AIAction> generatePossibleActions(AIPlayer player, GameState state, String playerId) {
        List<AIPlayer.AIAction> actions = new ArrayList<>();
        if (playerId == null) return actions;

        for (AIPlayer e : state.getEnemies(playerId))
            if (player.isInRange(e.getRobot())) actions.add(new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.ATTACK, e.getRobot()));

        Point pos = player.getPosition();
        for (int dx = -1; dx <= 1; dx++)
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                Point newPos = new Point(pos.x + dx, pos.y + dy);
                if (state.isValidPosition(newPos) && state.getRobotAt(newPos.x, newPos.y) == null)
                    actions.add(new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.MOVE, newPos));
            }

        actions.add(new AIPlayer.AIAction(AIPlayer.AIAction.ActionType.WAIT, null));
        return actions;
    }

    private GameState simulateAction(GameState original, AIPlayer player, AIPlayer.AIAction action) {
        GameState newState = original.copy();
        String robotId = newState.getIdByPlayer(player);
        if (robotId == null) return newState;

        if (action.getType() == AIPlayer.AIAction.ActionType.MOVE) {
            Point newPos = (Point) action.getTarget();
            if (newState.isValidPosition(newPos)) {
                newState.setRobotPosition(robotId, newPos);
                AIPlayer p = newState.getPlayerById(robotId);
                if (p != null) p.getRobot().setPosition(newPos.y, newPos.x);
            }
        } else if (action.getType() == AIPlayer.AIAction.ActionType.ATTACK) {
            Robot targetRobot = (Robot) action.getTarget();

            AIPlayer targetPlayer = newState.getPlayerByRobot(targetRobot);

            if (targetPlayer != null) {
                String targetId = newState.getIdByPlayer(targetPlayer);
                if (targetId != null) {
                    int currentHealth = newState.getRobotHealth(targetId);
                    int dmg = player.getDamage();
                    newState.setRobotHealth(targetId, currentHealth - dmg);
                    AIPlayer p = newState.getPlayerById(targetId);
                    if (p != null) p.getRobot().takeDamage(dmg);
                }
            }
        }

        return newState;
    }

    private AIPlayer findNearestEnemy(AIPlayer aiPlayer, List<AIPlayer> enemies) {
        AIPlayer nearest = null;
        double min = Double.MAX_VALUE;
        for (AIPlayer e : enemies) {
            double d = calculateDistance(aiPlayer.getPosition(), e.getPosition());
            if (d < min) {
                min = d;
                nearest = e;
            }
        }
        return nearest;
    }

    private AIPlayer findWeakestEnemy(List<AIPlayer> enemies) {
        AIPlayer weakest = null;
        int min = Integer.MAX_VALUE;
        for (AIPlayer e : enemies) {
            if (e.getHealth() < min) {
                min = e.getHealth();
                weakest = e;
            }
        }
        return weakest;
    }

    private Point findRetreatPosition(AIPlayer aiPlayer, List<AIPlayer> enemies, GameState state) {
        Point pos = aiPlayer.getPosition();
        Point best = pos;
        double max = 0;

        for (int dx = -2; dx <= 2; dx++)
            for (int dy = -2; dy <= 2; dy++) {
                Point candidate = new Point(pos.x + dx, pos.y + dy);
                if (!state.isValidPosition(candidate)) continue;

                double minDist = enemies.stream()
                        .mapToDouble(e -> calculateDistance(candidate, e.getPosition()))
                        .min().orElse(0);

                if (minDist > max) {
                    max = minDist;
                    best = candidate;
                }
            }
        return best;
    }

    private Point calculateMoveTowards(Point from, Point to) {
        int dx = Integer.compare(to.x, from.x);
        int dy = Integer.compare(to.y, from.y);
        return new Point(from.x + dx, from.y + dy);
    }

    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
}
