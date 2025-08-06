package game;

import java.awt.Point;
import java.util.*;

public class AIPlayer {
    private Robot robot;
    private AILevel aiLevel;
    private AIEngine aiEngine;

    public enum AILevel {
        EASY, MEDIUM, HARD
    }

    public AIPlayer(Robot robot, AILevel level) {
        this.robot = robot;
        this.aiLevel = level;
        this.aiEngine = new AIEngine(level);
    }

    public AIAction makeDecision(GameState gameState) {
        return aiEngine.calculateBestAction(this, gameState);
    }

    public boolean attack(Robot target) {
        if (isInRange(target)) {
            target.takeDamage(robot.getDamage());
            return true;
        }
        return false;
    }

    public void takeDamage(int damage) {
        robot.takeDamage(damage);
    }

    public boolean isInRange(Robot target) {
        double distance = calculateDistance(getPosition(), new Point(target.getCol(), target.getRow()));
        return distance <= robot.getRange();
    }

    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public void moveToPosition(Point newPosition) {
        robot.setPosition(newPosition.y, newPosition.x);
    }

    public boolean isAlive() {
        return robot.isAlive();
    }

    public Robot getRobot() {
        return robot;
    }

    public String getName() {
        return robot.getName();
    }

    public int getHealth() {
        return robot.getHealth();
    }

    public int getMaxHealth() {
        return 100;
    }

    public Point getPosition() {
        return new Point(robot.getCol(), robot.getRow());
    }

    public int getDamage() {
        return robot.getDamage();
    }

    public int getRange() {
        return robot.getRange();
    }

    public AILevel getAILevel() {
        return aiLevel;
    }

    static class AIEngine {
        private AILevel level;
        private Random random;

        public AIEngine(AILevel level) {
            this.level = level;
            this.random = new Random();
        }

        public AIAction calculateBestAction(AIPlayer aiPlayer, GameState gameState) {
            String myId = gameState.getIdByPlayer(aiPlayer);
            if (myId == null) return new AIAction(AIAction.ActionType.WAIT, null);

            switch (level) {
                case EASY:
                    return enhancedSimpleAI(aiPlayer, gameState, myId);
                case MEDIUM:
                    return enhancedIntermediateAI(aiPlayer, gameState, myId);
                case HARD:
                    return enhancedAdvancedAI(aiPlayer, gameState, myId);
                default:
                    return enhancedSimpleAI(aiPlayer, gameState, myId);
            }
        }

        private AIAction enhancedSimpleAI(AIPlayer aiPlayer, GameState gameState, String myId) {
            List<AIPlayer> enemies = gameState.getEnemies(myId);
            if (enemies.isEmpty()) return new AIAction(AIAction.ActionType.WAIT, null);

            // Enhanced targeting: prioritize weak enemies in range
            AIPlayer target = findBestTarget(aiPlayer, enemies);

            // More aggressive shooting - 80% chance if enemy in range
            if (aiPlayer.isInRange(target.getRobot()) && random.nextDouble() < 0.8) {
                System.out.println("AI EASY: دشمن در برد، شلیک دقیق!");
                return new AIAction(AIAction.ActionType.ATTACK, target);
            }

            // Enhanced movement: try multiple moves to get better positioning
            Point bestMove = findBestMovementPosition(aiPlayer, target, gameState, 2);
            if (bestMove != null) {
                System.out.println("AI EASY: حرکت تاکتیکی به " + bestMove);
                return new AIAction(AIAction.ActionType.MOVE, bestMove);
            }

            return new AIAction(AIAction.ActionType.WAIT, null);
        }

        private AIAction enhancedIntermediateAI(AIPlayer aiPlayer, GameState gameState, String myId) {
            List<AIPlayer> enemies = gameState.getEnemies(myId);
            if (enemies.isEmpty()) {
                System.out.println("AI MEDIUM: هیچ دشمنی پیدا نشد");
                return new AIAction(AIAction.ActionType.WAIT, null);
            }

            double healthRatio = (double) aiPlayer.getHealth() / aiPlayer.getMaxHealth();

            // Enhanced retreat logic - retreat earlier and smarter
            if (healthRatio < 0.4) {
                Point retreat = findOptimalRetreatPosition(aiPlayer, enemies, gameState);
                System.out.println("AI MEDIUM: عقب‌نشینی هوشمندانه به " + retreat);
                return new AIAction(AIAction.ActionType.MOVE, retreat);
            }

            // Smarter target selection
            AIPlayer target = selectOptimalTarget(aiPlayer, enemies);
            double distance = calculateDistance(aiPlayer.getPosition(), target.getPosition());

            // More accurate shooting - 90% chance with better targeting
            if (aiPlayer.isInRange(target.getRobot()) && random.nextDouble() < 0.9) {
                System.out.println("AI MEDIUM: شلیک دقیق به " + target.getName() +
                        " (سلامت: " + target.getHealth() + ")");
                return new AIAction(AIAction.ActionType.ATTACK, target);
            }

            // Advanced movement with tactical positioning
            Point tacticalMove = findTacticalPosition(aiPlayer, target, enemies, gameState);
            if (tacticalMove != null) {
                System.out.println("AI MEDIUM: حرکت تاکتیکی پیشرفته به " + tacticalMove);
                return new AIAction(AIAction.ActionType.MOVE, tacticalMove);
            }

            return new AIAction(AIAction.ActionType.WAIT, null);
        }

        private AIAction enhancedAdvancedAI(AIPlayer aiPlayer, GameState gameState, String myId) {
            List<AIPlayer> enemies = gameState.getEnemies(myId);
            if (enemies.isEmpty()) return new AIAction(AIAction.ActionType.WAIT, null);

            // Advanced threat assessment
            AIPlayer primaryThreat = assessPrimaryThreat(aiPlayer, enemies);
            double healthRatio = (double) aiPlayer.getHealth() / aiPlayer.getMaxHealth();

            // Sophisticated retreat with counter-attack positioning
            if (healthRatio < 0.5 && isInDanger(aiPlayer, enemies)) {
                Point strategicRetreat = findStrategicRetreatPosition(aiPlayer, enemies, gameState);
                System.out.println("AI HARD: عقب‌نشینی استراتژیک به " + strategicRetreat);
                return new AIAction(AIAction.ActionType.MOVE, strategicRetreat);
            }

            // Perfect accuracy for hard AI - 95% hit chance
            if (aiPlayer.isInRange(primaryThreat.getRobot()) && random.nextDouble() < 0.95) {
                System.out.println("AI HARD: شلیک دقیق و مرگبار به " + primaryThreat.getName());
                return new AIAction(AIAction.ActionType.ATTACK, primaryThreat);
            }

            // Multi-step tactical movement planning
            Point masterMove = planMultiStepMovement(aiPlayer, primaryThreat, enemies, gameState);
            if (masterMove != null) {
                System.out.println("AI HARD: حرکت استراتژیک چند مرحله‌ای به " + masterMove);
                return new AIAction(AIAction.ActionType.MOVE, masterMove);
            }

            // Fallback to minimax for complex situations
            return minimaxDecision(aiPlayer, gameState, 4, myId);
        }

        // Enhanced helper methods for better targeting
        private AIPlayer findBestTarget(AIPlayer aiPlayer, List<AIPlayer> enemies) {
            AIPlayer bestTarget = null;
            double bestScore = -1;

            for (AIPlayer enemy : enemies) {
                double distance = calculateDistance(aiPlayer.getPosition(), enemy.getPosition());
                double healthFactor = (100 - enemy.getHealth()) / 100.0; // Prefer weak enemies
                double rangeFactor = aiPlayer.isInRange(enemy.getRobot()) ? 2.0 : 1.0; // Prefer in-range targets
                double distanceFactor = 1.0 / (distance + 1); // Prefer closer enemies

                double score = healthFactor * rangeFactor * distanceFactor;
                if (score > bestScore) {
                    bestScore = score;
                    bestTarget = enemy;
                }
            }

            return bestTarget != null ? bestTarget : enemies.get(0);
        }

        private AIPlayer selectOptimalTarget(AIPlayer aiPlayer, List<AIPlayer> enemies) {
            // Priority: In range + low health > In range + high health > Close + low health
            List<AIPlayer> inRangeEnemies = new ArrayList<>();
            List<AIPlayer> closeEnemies = new ArrayList<>();

            for (AIPlayer enemy : enemies) {
                if (aiPlayer.isInRange(enemy.getRobot())) {
                    inRangeEnemies.add(enemy);
                } else if (calculateDistance(aiPlayer.getPosition(), enemy.getPosition()) <= 3) {
                    closeEnemies.add(enemy);
                }
            }

            if (!inRangeEnemies.isEmpty()) {
                return inRangeEnemies.stream()
                        .min(Comparator.comparingInt(AIPlayer::getHealth))
                        .orElse(inRangeEnemies.get(0));
            }

            if (!closeEnemies.isEmpty()) {
                return closeEnemies.stream()
                        .min(Comparator.comparingInt(AIPlayer::getHealth))
                        .orElse(closeEnemies.get(0));
            }

            return findWeakestEnemy(enemies);
        }

        private AIPlayer assessPrimaryThreat(AIPlayer aiPlayer, List<AIPlayer> enemies) {
            double maxThreatLevel = -1;
            AIPlayer primaryThreat = null;

            for (AIPlayer enemy : enemies) {
                double distance = calculateDistance(aiPlayer.getPosition(), enemy.getPosition());
                double healthRatio = enemy.getHealth() / 100.0;
                double damageRatio = enemy.getDamage() / 20.0;

                // Threat calculation: closer + healthier + stronger = more dangerous
                double threatLevel = (healthRatio * damageRatio) / (distance + 1);

                if (threatLevel > maxThreatLevel) {
                    maxThreatLevel = threatLevel;
                    primaryThreat = enemy;
                }
            }

            return primaryThreat != null ? primaryThreat : enemies.get(0);
        }

        // Enhanced movement methods
        private Point findBestMovementPosition(AIPlayer aiPlayer, AIPlayer target, GameState gameState, int searchRadius) {
            Point currentPos = aiPlayer.getPosition();
            Point targetPos = target.getPosition();
            Point bestMove = null;
            double bestScore = -1;

            for (int dx = -searchRadius; dx <= searchRadius; dx++) {
                for (int dy = -searchRadius; dy <= searchRadius; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    Point candidate = new Point(currentPos.x + dx, currentPos.y + dy);
                    if (!gameState.isValidPosition(candidate) ||
                            gameState.getRobotAt(candidate.x, candidate.y) != null) continue;

                    double newDistance = calculateDistance(candidate, targetPos);
                    double optimalDistance = aiPlayer.getRange() - 0.5; // Stay just within range

                    // Score based on how close to optimal shooting distance
                    double score = 1.0 / (Math.abs(newDistance - optimalDistance) + 0.1);

                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = candidate;
                    }
                }
            }

            return bestMove;
        }

        private Point findTacticalPosition(AIPlayer aiPlayer, AIPlayer target, List<AIPlayer> enemies, GameState gameState) {
            Point currentPos = aiPlayer.getPosition();
            Point bestPosition = null;
            double bestScore = -1;

            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx == 0 && dy == 0) continue;

                    Point candidate = new Point(currentPos.x + dx, currentPos.y + dy);
                    if (!gameState.isValidPosition(candidate) ||
                            gameState.getRobotAt(candidate.x, candidate.y) != null) continue;

                    double score = evaluatePosition(candidate, target, enemies, aiPlayer);

                    if (score > bestScore) {
                        bestScore = score;
                        bestPosition = candidate;
                    }
                }
            }

            return bestPosition;
        }

        private double evaluatePosition(Point position, AIPlayer target, List<AIPlayer> enemies, AIPlayer aiPlayer) {
            double score = 0;

            // Distance to primary target
            double targetDistance = calculateDistance(position, target.getPosition());
            double optimalRange = aiPlayer.getRange() - 0.5;

            if (targetDistance <= optimalRange) {
                score += 50; // Bonus for being in shooting range
            }

            score += 1.0 / (Math.abs(targetDistance - optimalRange) + 0.1) * 10;

            // Distance from all enemies (prefer positions that maintain distance)
            double minEnemyDistance = enemies.stream()
                    .mapToDouble(e -> calculateDistance(position, e.getPosition()))
                    .min().orElse(0);

            if (minEnemyDistance > 1) {
                score += minEnemyDistance * 5; // Bonus for keeping distance
            }

            return score;
        }

        private Point planMultiStepMovement(AIPlayer aiPlayer, AIPlayer target, List<AIPlayer> enemies, GameState gameState) {
            // Look ahead 2-3 moves to find optimal positioning
            Point currentPos = aiPlayer.getPosition();
            Point targetPos = target.getPosition();

            // Try to position for optimal shooting angle while maintaining safety
            List<Point> candidates = getExtendedValidMoves(aiPlayer, gameState, 2);

            return candidates.stream()
                    .max(Comparator.comparingDouble(pos -> {
                        double targetScore = evaluatePosition(pos, target, enemies, aiPlayer);
                        double safetyScore = calculateSafetyScore(pos, enemies);
                        return targetScore + safetyScore * 0.3;
                    }))
                    .orElse(null);
        }

        private List<Point> getExtendedValidMoves(AIPlayer aiPlayer, GameState gameState, int radius) {
            List<Point> validMoves = new ArrayList<>();
            Point pos = aiPlayer.getPosition();

            // Check moves in expanding circles
            for (int r = 1; r <= radius; r++) {
                for (int dx = -r; dx <= r; dx++) {
                    for (int dy = -r; dy <= r; dy++) {
                        if (Math.abs(dx) + Math.abs(dy) != r) continue; // Only check perimeter

                        Point newPos = new Point(pos.x + dx, pos.y + dy);
                        if (gameState.isValidPosition(newPos) &&
                                gameState.getRobotAt(newPos.x, newPos.y) == null) {
                            validMoves.add(newPos);
                        }
                    }
                }
            }

            return validMoves;
        }

        private double calculateSafetyScore(Point position, List<AIPlayer> enemies) {
            return enemies.stream()
                    .mapToDouble(enemy -> calculateDistance(position, enemy.getPosition()))
                    .min().orElse(0);
        }

        private boolean isInDanger(AIPlayer aiPlayer, List<AIPlayer> enemies) {
            return enemies.stream()
                    .anyMatch(enemy -> enemy.isInRange(aiPlayer.getRobot()) && enemy.getHealth() > 30);
        }

        private Point findOptimalRetreatPosition(AIPlayer aiPlayer, List<AIPlayer> enemies, GameState gameState) {
            Point pos = aiPlayer.getPosition();
            Point bestRetreat = pos;
            double maxSafety = 0;

            for (int dx = -3; dx <= 3; dx++) {
                for (int dy = -3; dy <= 3; dy++) {
                    Point candidate = new Point(pos.x + dx, pos.y + dy);
                    if (!gameState.isValidPosition(candidate) ||
                            gameState.getRobotAt(candidate.x, candidate.y) != null) continue;

                    double minEnemyDistance = enemies.stream()
                            .mapToDouble(e -> calculateDistance(candidate, e.getPosition()))
                            .min().orElse(0);

                    // Prefer positions that are far from enemies but still allow counter-attack
                    double safety = minEnemyDistance;
                    if (minEnemyDistance > aiPlayer.getRange()) {
                        safety *= 0.7; // Slight penalty for being too far
                    }

                    if (safety > maxSafety) {
                        maxSafety = safety;
                        bestRetreat = candidate;
                    }
                }
            }

            return bestRetreat;
        }

        private Point findStrategicRetreatPosition(AIPlayer aiPlayer, List<AIPlayer> enemies, GameState gameState) {
            // Advanced retreat that considers future positioning
            Point optimalRetreat = findOptimalRetreatPosition(aiPlayer, enemies, gameState);

            // Check if retreat position allows for future counter-attack
            AIPlayer weakestEnemy = findWeakestEnemy(enemies);
            double distanceToWeak = calculateDistance(optimalRetreat, weakestEnemy.getPosition());

            if (distanceToWeak > aiPlayer.getRange() * 1.5) {
                // Find a compromise position
                return findBestMovementPosition(aiPlayer, weakestEnemy, gameState, 3);
            }

            return optimalRetreat;
        }

        // Keep existing methods but enhance them
        private AIAction minimaxDecision(AIPlayer aiPlayer, GameState gameState, int depth, String myId) {
            List<AIAction> actions = generatePossibleActions(aiPlayer, gameState, myId);
            AIAction best = null;
            int bestScore = Integer.MIN_VALUE;

            for (AIAction action : actions) {
                GameState newState = simulateAction(gameState, aiPlayer, action);
                int score = minimaxValue(newState, depth - 1, false, aiPlayer);
                if (score > bestScore) {
                    bestScore = score;
                    best = action;
                }
            }

            return best != null ? best : new AIAction(AIAction.ActionType.WAIT, null);
        }

        private int minimaxValue(GameState state, int depth, boolean isMax, AIPlayer origin) {
            if (depth == 0 || state.isGameOver())
                return evaluateGameState(state, origin);

            if (isMax) {
                int max = Integer.MIN_VALUE;
                for (AIAction a : generatePossibleActions(origin, state, state.getIdByPlayer(origin))) {
                    GameState s = simulateAction(state, origin, a);
                    max = Math.max(max, minimaxValue(s, depth - 1, false, origin));
                }
                return max;
            } else {
                int min = Integer.MAX_VALUE;
                String originId = state.getIdByPlayer(origin);
                if (originId == null) return 0;

                for (AIPlayer enemy : state.getEnemies(originId)) {
                    for (AIAction a : generatePossibleActions(enemy, state, state.getIdByPlayer(enemy))) {
                        GameState s = simulateAction(state, enemy, a);
                        min = Math.min(min, minimaxValue(s, depth - 1, true, origin));
                    }
                }
                return min;
            }
        }

        private int evaluateGameState(GameState state, AIPlayer player) {
            int score = player.getHealth() * 3; // Increased health value

            String playerId = state.getIdByPlayer(player);
            if (playerId == null) return score;

            for (AIPlayer enemy : state.getEnemies(playerId)) {
                double d = calculateDistance(player.getPosition(), enemy.getPosition());
                if (d <= player.getRange()) score += 60; // Increased shooting bonus
                if (d < 2 && enemy.getHealth() > player.getHealth()) score -= 40; // Increased danger penalty
                score += (100 - enemy.getHealth()); // Bonus for enemy damage
            }

            return score;
        }

        private List<AIAction> generatePossibleActions(AIPlayer player, GameState state, String playerId) {
            List<AIAction> actions = new ArrayList<>();
            if (playerId == null) return actions;

            // Enhanced action generation with better movement options
            for (AIPlayer e : state.getEnemies(playerId))
                if (player.isInRange(e.getRobot())) actions.add(new AIAction(AIAction.ActionType.ATTACK, e));

            Point pos = player.getPosition();
            // Expanded movement range for more mobility
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -2; dy <= 2; dy++) {
                    if (dx == 0 && dy == 0) continue;
                    Point newPos = new Point(pos.x + dx, pos.y + dy);
                    if (state.isValidPosition(newPos) && state.getRobotAt(newPos.x, newPos.y) == null)
                        actions.add(new AIAction(AIAction.ActionType.MOVE, newPos));
                }
            }

            actions.add(new AIAction(AIAction.ActionType.WAIT, null));
            return actions;
        }

        private GameState simulateAction(GameState original, AIPlayer player, AIAction action) {
            GameState newState = original.copy();
            String robotId = newState.getIdByPlayer(player);
            if (robotId == null) return newState;

            if (action.getType() == AIAction.ActionType.MOVE) {
                Point newPos = (Point) action.getTarget();
                if (newState.isValidPosition(newPos)) {
                    newState.setRobotPosition(robotId, newPos);
                    AIPlayer p = newState.getPlayerById(robotId);
                    if (p != null) p.getRobot().setPosition(newPos.y, newPos.x);
                }
            } else if (action.getType() == AIAction.ActionType.ATTACK) {
                AIPlayer targetPlayer = (AIPlayer) action.getTarget();
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

        private Point calculateMoveTowards(Point from, Point to) {
            int dx = Integer.compare(to.x, from.x);
            int dy = Integer.compare(to.y, from.y);
            return new Point(from.x + dx, from.y + dy);
        }

        private double calculateDistance(Point p1, Point p2) {
            return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        }
    }

    public static class AIAction {
        public enum ActionType {
            MOVE, ATTACK, WAIT
        }

        private ActionType type;
        private Object target;

        public AIAction(ActionType type, Object target) {
            this.type = type;
            this.target = target;
        }

        public ActionType getType() { return type; }
        public Object getTarget() { return target; }
    }
}