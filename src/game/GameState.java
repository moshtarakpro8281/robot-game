package game;

import java.awt.Point;
import java.util.*;

public class GameState {

    private Map<String, Point> robotPositions;
    private Map<String, Integer> robotHealth;
    private Set<String> deadRobots;
    private int width, height;
    private Set<Point> walls;
    private Map<String, AIPlayer> idToPlayer;
    private Map<AIPlayer, String> playerToId;

    public GameState(int width, int height) {
        this.width = width;
        this.height = height;
        this.robotPositions = new HashMap<>();
        this.robotHealth = new HashMap<>();
        this.deadRobots = new HashSet<>();
        this.walls = new HashSet<>();
        this.idToPlayer = new HashMap<>();
        this.playerToId = new HashMap<>();
    }

    public GameState copy() {
        GameState copy = new GameState(width, height);
        for (Map.Entry<String, Point> e : robotPositions.entrySet()) {
            copy.robotPositions.put(e.getKey(), new Point(e.getValue()));
        }
        copy.robotHealth.putAll(robotHealth);
        copy.deadRobots.addAll(deadRobots);
        copy.walls.addAll(walls);
        copy.idToPlayer.putAll(this.idToPlayer);
        copy.playerToId.putAll(this.playerToId);
        return copy;
    }

    public void addPlayer(String id, AIPlayer player) {
        idToPlayer.put(id, player);
        playerToId.put(player, id);
        setRobotPosition(id, player.getPosition());
        setRobotHealth(id, player.getHealth());
    }

    public String getIdByPlayer(AIPlayer player) {
        String id = playerToId.get(player);
        if (id == null) {
            System.out.println("Warning: No ID found for AIPlayer " + player.getName());
        }
        return id;
    }

    public AIPlayer getPlayerById(String id) {
        AIPlayer player = idToPlayer.get(id);
        if (player == null) {
            System.out.println("Warning: No AIPlayer found for ID " + id);
        }
        return player;
    }

    // متد کمکی برای تبدیل Robot به AIPlayer
    public AIPlayer getPlayerByRobot(Robot robot) {
        for (AIPlayer player : idToPlayer.values()) {
            if (player.getRobot() == robot) {
                return player;
            }
        }
        return null;
    }

    public List<AIPlayer> getEnemies(String robotId) {
        List<AIPlayer> enemies = new ArrayList<>();
        for (Map.Entry<String, AIPlayer> entry : idToPlayer.entrySet()) {
            String id = entry.getKey();
            if (!id.equals(robotId) && !deadRobots.contains(id)) {
                enemies.add(entry.getValue());
            }
        }
        return enemies;
    }

    public void setRobotPosition(String robotId, Point position) {
        robotPositions.put(robotId, position);
    }

    public Point getRobotPosition(String robotId) {
        return robotPositions.get(robotId);
    }

    public void setRobotHealth(String robotId, int health) {
        robotHealth.put(robotId, health);
        if (health <= 0) deadRobots.add(robotId);
    }

    public int getRobotHealth(String robotId) {
        return robotHealth.getOrDefault(robotId, 0);
    }

    public boolean isRobotDead(String robotId) {
        return deadRobots.contains(robotId);
    }

    public void addWall(Point wall) {
        walls.add(wall);
    }

    private boolean inBounds(Point p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }

    public boolean isWall(Point p) {
        return !inBounds(p) || walls.contains(p);
    }

    public boolean isValidPosition(Point p) {
        return inBounds(p) && !walls.contains(p);
    }

    public boolean isGameOver() {
        int aliveCount = 0;
        for (String id : idToPlayer.keySet()) {
            if (!isRobotDead(id)) aliveCount++;
        }
        return aliveCount <= 1;
    }

    public AIPlayer getRobotAt(int x, int y) {
        Point target = new Point(x, y);
        for (Map.Entry<String, Point> entry : robotPositions.entrySet()) {
            if (deadRobots.contains(entry.getKey())) continue;
            if (entry.getValue().equals(target)) {
                return idToPlayer.get(entry.getKey());
            }
        }
        return null;
    }

    public List<AIPlayer> getAllRobots() {
        List<AIPlayer> list = new ArrayList<>();
        for (Map.Entry<String, AIPlayer> entry : idToPlayer.entrySet()) {
            if (!isRobotDead(entry.getKey())) {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    public boolean canMove(AIPlayer player, Direction dir) {
        String id = getIdByPlayer(player);
        if (id == null) return false;

        Point pos = robotPositions.get(id);
        if (pos == null) return false;

        Point newPos = new Point(pos);
        switch (dir) {
            case UP:    newPos.y--; break;
            case DOWN:  newPos.y++; break;
            case LEFT:  newPos.x--; break;
            case RIGHT: newPos.x++; break;
        }

        return isValidPosition(newPos) && getRobotAt(newPos.x, newPos.y) == null;
    }

    // متد به‌روزشده برای ساخت GameState از لیست AIPlayer ها و نقشه
    public static GameState fromCurrentState(List<AIPlayer> aiPlayers, Cell[][] map, int width, int height) {
        GameState gameState = new GameState(width, height);

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Entity entity = map[r][c].getEntity();
                if (entity instanceof Obstacle) {
                    gameState.addWall(new Point(c, r));
                }
            }
        }

        for (AIPlayer player : aiPlayers) {
            String id = player.getName(); // یا هر شناسه منحصربفرد که دارید
            gameState.addPlayer(id, player);
        }

        return gameState;
    }
}
