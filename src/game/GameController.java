package game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import game.Robot.Direction;

public class GameController {

    @FXML private GridPane mapGrid;
    @FXML private Button moveUpButton, moveDownButton, moveLeftButton, moveRightButton, shootButton;
    @FXML private Label ammoLabel, activeAmmoLabel;
    @FXML private ProgressBar health1, health2, health3, health4;

    private final int mapHeight = 15;
    private final int mapWidth = 15;
    private Cell[][] map = new Cell[mapHeight][mapWidth];

    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private int currentRobotIndex = 0;

    private int globalRobotTurnIndex = 0;

    private int currentRow;
    private int currentCol;
    private Circle bullet;

    private boolean shotFired = false;
    private boolean isPlayerTurnActive = false;

    private List<AIPlayer> aiPlayers = new ArrayList<>();

    private Timeline shootingTimeline;

    // Add game mode field
    private String gameMode = "pvp"; // Default game mode

    // Add this method to set the game mode from settings
    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
        System.out.println("Game mode set to: " + gameMode);
    }

    @FXML
    public void initialize() {
        initMap();
        // Don't initialize players here - do it after game mode is set
        renderMap();
    }

    // Call this method after setting the game mode
    public void startGame() {
        initPlayersAndRobots();
        initAIPlayers();
        updateUI();

        mapGrid.setFocusTraversable(true);
        mapGrid.requestFocus();

        mapGrid.setOnKeyPressed(this::handleKeyPressed);

        startTurnForCurrentRobot();
    }

    private void initAIPlayers() {
        aiPlayers.clear();
        for (Player p : players) {
            if (p.isAI()) {
                for (Robot r : p.getRobots()) {
                    AIPlayer ai = new AIPlayer(r, AIPlayer.AILevel.MEDIUM);
                    aiPlayers.add(ai);
                }
            }
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        if (shotFired) return;

        Player currentPlayer = players.get(currentPlayerIndex);
        if (currentPlayer.isAI()) return;
        if (!isPlayerTurnActive) return;

        KeyCode code = event.getCode();

        if (currentPlayerIndex == 0) {
            switch (code) {
                case W -> moveRobot(0, -1, Direction.UP);
                case A -> moveRobot(-1, 0, Direction.LEFT);
                case S -> moveRobot(0, 1, Direction.DOWN);
                case D -> moveRobot(1, 0, Direction.RIGHT);
                case C -> shoot();
                case R -> reloadAmmo();
            }
        } else if (currentPlayerIndex == 1) {
            switch (code) {
                case UP -> moveRobot(0, -1, Direction.UP);
                case LEFT -> moveRobot(-1, 0, Direction.LEFT);
                case DOWN -> moveRobot(0, 1, Direction.DOWN);
                case RIGHT -> moveRobot(1, 0, Direction.RIGHT);
                case M -> shoot();
            }
        }
    }

    private void reloadAmmo() {
        Robot robot = getCurrentRobot();
        robot.setAmmo(10);
        // Optional: Reset damage when reloading
        robot.resetDamage();
        updateUI();
        System.out.println("مهمات ربات " + robot.getName() + " پر شد و قدرت آسیب بازگردانده شد.");
    }

    private void initMap() {
        for (int r = 0; r < mapHeight; r++) {
            for (int c = 0; c < mapWidth; c++) {
                map[r][c] = new Cell(r, c);
            }
        }
    }

    // Updated method to use the game mode
    private void initPlayersAndRobots() {
        Player p1, p2;

        // Create players based on the selected game mode
        switch (gameMode) {
            case "pvp":
                // Player vs Player
                p1 = new Player("Player1", false); // Human player
                p2 = new Player("Player2", false); // Human player
                System.out.println("Initialized Player vs Player mode");
                break;
            case "pvAI":
                // Player vs AI
                p1 = new Player("Player1", false); // Human player
                p2 = new Player("Player2", true);  // AI player
                System.out.println("Initialized Player vs AI mode");
                break;
            case "aiVsAi":
                // AI vs AI
                p1 = new Player("Player1", true);  // AI player
                p2 = new Player("Player2", true);  // AI player
                System.out.println("Initialized AI vs AI mode");
                break;
            default:
                // Default to PvP if unknown mode
                p1 = new Player("Player1", false);
                p2 = new Player("Player2", false);
                System.out.println("Unknown game mode, defaulting to Player vs Player");
                break;
        }

        // Create robots (same as before)
        Robot r1 = new Robot("R1", 0, 0, Color.RED);
        Robot r2 = new Robot("R2", 0, 1, Color.RED);
        Robot r3 = new Robot("R3", 14, 14, Color.BLUE);
        Robot r4 = new Robot("R4", 14, 13, Color.BLUE);

        // Set robot properties (same as before)
        r1.setAmmo(10);
        r1.setDirection(Direction.RIGHT);
        r1.setRange(3);
        r1.setDamage(20);

        r2.setAmmo(10);
        r2.setDirection(Direction.RIGHT);
        r2.setRange(3);
        r2.setDamage(20);

        r3.setAmmo(10);
        r3.setDirection(Direction.LEFT);
        r3.setRange(3);
        r3.setDamage(20);

        r4.setAmmo(10);
        r4.setDirection(Direction.LEFT);
        r4.setRange(3);
        r4.setDamage(20);

        // Add robots to players
        p1.addRobot(r1);
        p1.addRobot(r2);
        p2.addRobot(r3);
        p2.addRobot(r4);

        // Place robots on map
        map[r1.getRow()][r1.getCol()].setEntity(r1);
        map[r2.getRow()][r2.getCol()].setEntity(r2);
        map[r3.getRow()][r3.getCol()].setEntity(r3);
        map[r4.getRow()][r4.getCol()].setEntity(r4);

        players.add(p1);
        players.add(p2);

        System.out.println("Game initialized with mode: " + gameMode);
    }

    private void renderMap() {
        mapGrid.getChildren().clear();

        for (int r = 0; r < mapHeight; r++) {
            for (int c = 0; c < mapWidth; c++) {
                Rectangle base = new Rectangle(30, 30, Color.LIGHTGRAY);
                base.setStroke(Color.BLACK);
                mapGrid.add(base, c, r);

                Entity entity = map[r][c].getEntity();
                if (entity instanceof Obstacle) {
                    mapGrid.add(((Obstacle) entity).getView(), c, r);
                } else if (entity instanceof Robot) {
                    Circle robotView = new Circle(12, ((Robot) entity).getColor());
                    mapGrid.add(robotView, c, r);
                }
            }
        }

        if (bullet != null) {
            mapGrid.add(bullet, currentCol, currentRow);
        }
    }

    private Robot getCurrentRobot() {
        Player currentPlayer = players.get(currentPlayerIndex);
        return currentPlayer.getRobots().get(currentRobotIndex);
    }

    private void nextTurn() {
        shotFired = false;
        globalRobotTurnIndex++;

        int totalRobots = 0;
        for (Player p : players) totalRobots += p.getRobots().size();

        for (int i = 0; i < totalRobots; i++) {
            int idx = globalRobotTurnIndex % totalRobots;

            int count = 0;
            boolean found = false;
            for (int pIndex = 0; pIndex < players.size(); pIndex++) {
                Player p = players.get(pIndex);
                int size = p.getRobots().size();
                if (idx < count + size) {
                    currentPlayerIndex = pIndex;
                    currentRobotIndex = idx - count;
                    if (getCurrentRobot().getHealth() > 0) {
                        found = true;
                        break;
                    }
                }
                count += size;
            }

            if (found) break;

            globalRobotTurnIndex++;
        }

        startTurnForCurrentRobot();
    }

    private void startTurnForCurrentRobot() {
        updateUI();
        renderMap();

        Player currentPlayer = players.get(currentPlayerIndex);
        Robot robot = getCurrentRobot();

        if (robot.getHealth() <= 0) {
            System.out.println("ربات " + robot.getName() + " مرده، نوبت بعدی");
            nextTurn();
            return;
        }

        if (currentPlayer.isAI()) {
            isPlayerTurnActive = false;
            // اجرای تاخیر برای نوبت هوش مصنوعی
            Timeline delay = new Timeline(new KeyFrame(Duration.millis(800), e -> performAITurn()));
            delay.play();
        } else {
            isPlayerTurnActive = true;
            mapGrid.requestFocus();
        }
    }

    private void performAITurn() {
        Robot currentRobot = getCurrentRobot();
        AIPlayer aiPlayer = findAIPlayerByName(currentRobot.getName());
        if (aiPlayer == null) {
            System.out.println("AIPlayer برای " + currentRobot.getName() + " پیدا نشد!");
            nextTurn();
            return;
        }

        GameState gameState = buildGameStateFromCurrent();
        AIPlayer.AIAction action = aiPlayer.makeDecision(gameState);

        System.out.println("AI تصمیم " + currentRobot.getName() + ": " + action.getType());

        switch (action.getType()) {
            case MOVE -> {
                Point p = (Point) action.getTarget();
                int dx = p.x - currentRobot.getCol();
                int dy = p.y - currentRobot.getRow();

                Direction dir = calculateDirection(dx, dy);
                moveRobotAI(dx, dy, dir);
            }
            case ATTACK -> {
                AIPlayer targetAI = (AIPlayer) action.getTarget();
                Robot targetRobot = findRobotByName(targetAI.getName());
                if (targetRobot != null) {
                    Direction dir = calculateDirection(
                            targetRobot.getCol() - currentRobot.getCol(),
                            targetRobot.getRow() - currentRobot.getRow()
                    );
                    currentRobot.setDirection(dir);
                    shoot();
                } else {
                    nextTurn();
                }
            }
            case WAIT -> nextTurn();
        }
    }

    private void moveRobotAI(int dx, int dy, Direction direction) {
        Robot robot = getCurrentRobot();
        int newRow = robot.getRow() + dy;
        int newCol = robot.getCol() + dx;

        if (isInBounds(newRow, newCol) && map[newRow][newCol].isEmpty()) {
            map[robot.getRow()][robot.getCol()].removeEntity();
            robot.setPosition(newRow, newCol);
            robot.setDirection(direction);
            map[newRow][newCol].setEntity(robot);

            AIPlayer ai = findAIPlayerByName(robot.getName());
            if (ai != null) {
                ai.moveToPosition(new Point(newCol, newRow));
            }

            updateUI();
            renderMap();

            Timeline delayTimeline = new Timeline(new KeyFrame(Duration.millis(500), e -> nextTurn()));
            delayTimeline.play();
        } else {
            nextTurn();
        }
    }

    private AIPlayer findAIPlayerByName(String name) {
        for (AIPlayer ai : aiPlayers) {
            if (ai.getName().equals(name)) return ai;
        }
        return null;
    }

    private Robot findRobotByName(String name) {
        for (Player p : players) {
            for (Robot r : p.getRobots()) {
                if (r.getName().equals(name)) return r;
            }
        }
        return null;
    }

    private Direction calculateDirection(int dx, int dy) {
        if (dx == 0 && dy == 0) return Direction.RIGHT;
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle >= -45 && angle < 45) return Direction.RIGHT;
        if (angle >= 45 && angle < 135) return Direction.DOWN;
        if (angle >= 135 || angle < -135) return Direction.LEFT;
        return Direction.UP;
    }

    private GameState buildGameStateFromCurrent() {
        List<AIPlayer> currentAIPlayers = new ArrayList<>();
        for (Player p : players) {
            if (p.isAI()) {
                for (Robot r : p.getRobots()) {
                    AIPlayer ai = findAIPlayerByName(r.getName());
                    if (ai != null) {
                        currentAIPlayers.add(ai);
                    } else {
                        currentAIPlayers.add(new AIPlayer(r, AIPlayer.AILevel.MEDIUM));
                    }
                }
            }
        }
        return GameState.fromCurrentState(currentAIPlayers, map, mapWidth, mapHeight);
    }

    private void updateUI() {
        // Check if players list is empty (before initialization)
        if (players.isEmpty()) {
            return;
        }

        Robot robot = getCurrentRobot();
        boolean alive = robot.getHealth() > 0;
        boolean enableControls = alive && !shotFired && isPlayerTurnActive;

        moveUpButton.setDisable(!enableControls);
        moveDownButton.setDisable(!enableControls);
        moveLeftButton.setDisable(!enableControls);
        moveRightButton.setDisable(!enableControls);
        shootButton.setDisable(!enableControls);

        int ammo = robot.getAmmo();
        ammoLabel.setText("Ammo: " + (ammo == Integer.MAX_VALUE ? "∞" : ammo));
        // Show current damage in the UI
        activeAmmoLabel.setText("Active: " + robot.getName() + " (" + robot.getDirection() + ") - Damage: " + robot.getCurrentDamage());

        if (players.get(0).getRobots().size() >= 2) {
            health1.setProgress(players.get(0).getRobots().get(0).getHealth() / 100.0);
            health2.setProgress(players.get(0).getRobots().get(1).getHealth() / 100.0);
        }
        if (players.get(1).getRobots().size() >= 2) {
            health3.setProgress(players.get(1).getRobots().get(0).getHealth() / 100.0);
            health4.setProgress(players.get(1).getRobots().get(1).getHealth() / 100.0);
        }
    }

    @FXML private void moveUp() { if (!shotFired) moveRobot(0, -1, Direction.UP); }
    @FXML private void moveDown() { if (!shotFired) moveRobot(0, 1, Direction.DOWN); }
    @FXML private void moveLeft() { if (!shotFired) moveRobot(-1, 0, Direction.LEFT); }
    @FXML private void moveRight() { if (!shotFired) moveRobot(1, 0, Direction.RIGHT); }

    private void moveRobot(int dx, int dy, Direction direction) {
        if (shotFired) return;
        if (!isPlayerTurnActive) return;

        Robot robot = getCurrentRobot();
        int newRow = robot.getRow() + dy;
        int newCol = robot.getCol() + dx;

        if (isInBounds(newRow, newCol) && map[newRow][newCol].isEmpty()) {
            map[robot.getRow()][robot.getCol()].removeEntity();
            robot.setPosition(newRow, newCol);
            robot.setDirection(direction);
            map[newRow][newCol].setEntity(robot);

            updateUI();
            renderMap();
        } else {
            System.out.println("حرکت غیرمجاز یا مکان اشغال شده.");
        }
    }

    @FXML
    private void shoot() {
        if (shotFired) return;
        if (!isPlayerTurnActive) return;

        Robot shooter = getCurrentRobot();
        System.out.println("شلیک توسط " + shooter.getName() + " - جهت: " + shooter.getDirection() + " - قدرت آسیب: " + shooter.getCurrentDamage());

        if (shooter.getAmmo() == 0) {
            System.out.println("مهمات تمام است!");
            return;
        } else if (shooter.getAmmo() != Integer.MAX_VALUE) {
            shooter.setAmmo(shooter.getAmmo() - 1);
        }

        currentRow = shooter.getRow();
        currentCol = shooter.getCol();
        int dRow = 0, dCol = 0;

        if (shooter.getDirection() == null) {
            System.out.println("⚠️ جهت ربات تنظیم نشده است! جهت پیش‌فرض: RIGHT");
            shooter.setDirection(Direction.RIGHT);
        }

        switch (shooter.getDirection()) {
            case UP -> dRow = -1;
            case DOWN -> dRow = 1;
            case LEFT -> dCol = -1;
            case RIGHT -> dCol = 1;
        }

        bullet = new Circle(5, Color.BLACK);
        mapGrid.add(bullet, currentCol, currentRow);

        shotFired = true;
        updateUI();

        Timeline timeline = new Timeline();
        final int finalDRow = dRow;
        final int finalDCol = dCol;

        final int maxRange = shooter.getRange();
        final int[] steps = {0};

        KeyFrame keyFrame = new KeyFrame(Duration.millis(100), e -> {
            currentRow += finalDRow;
            currentCol += finalDCol;
            steps[0]++;

            System.out.println("موقعیت تیر: (" + currentRow + ", " + currentCol + ")");

            if (!isInBounds(currentRow, currentCol) || steps[0] > maxRange) {
                timeline.stop();
                mapGrid.getChildren().remove(bullet);
                bullet = null;
                isPlayerTurnActive = false;
                nextTurn();
                return;
            }

            Cell target = map[currentRow][currentCol];
            Entity entity = target.getEntity();

            if (entity instanceof Obstacle) {
                ((Obstacle) entity).applyEffect(shooter);
                timeline.stop();
                mapGrid.getChildren().remove(bullet);
                bullet = null;
                isPlayerTurnActive = false;
                nextTurn();
                return;
            }

            if (entity instanceof Robot) {
                Robot hit = (Robot) entity;
                Robot currentShooter = getCurrentRobot();

                // Calculate current damage (decreases by 5% with each shot fired)
                int currentDamage = currentShooter.getCurrentDamage();

                hit.takeDamage(currentDamage);
                System.out.println("💥 " + currentShooter.getName() + " hit " + hit.getName() + " for " + currentDamage + " damage!");

                // Reduce shooter's damage by 5% for next shot
                currentShooter.reduceDamageBy5Percent();
                System.out.println("🔽 " + currentShooter.getName() + "'s damage reduced to " + currentShooter.getCurrentDamage());

                AIPlayer ai = findAIPlayerByName(hit.getName());
                if (ai != null) {
                    ai.takeDamage(currentDamage);
                }
                if (hit.getHealth() <= 0) {
                    target.removeEntity();
                    System.out.println("💀 ربات " + hit.getName() + " نابود شد!");

                    for (Player p : players) {
                        p.getRobots().removeIf(r -> r.getName().equals(hit.getName()));
                    }
                }
                timeline.stop();
                mapGrid.getChildren().remove(bullet);
                bullet = null;
                isPlayerTurnActive = false;
                nextTurn();
                return;
            }

            mapGrid.getChildren().remove(bullet);
            mapGrid.add(bullet, currentCol, currentRow);
            updateUI();
        });

        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        shootingTimeline = timeline;
    }

    private boolean isInBounds(int r, int c) {
        return r >= 0 && r < mapHeight && c >= 0 && c < mapWidth;
    }

    @FXML
    private void selectRobot1() {
        selectRobot(0, 0);
    }

    @FXML
    private void selectRobot2() {
        selectRobot(0, 1);
    }

    @FXML
    private void selectRobot3() {
        selectRobot(1, 0);
    }

    @FXML
    private void selectRobot4() {
        selectRobot(1, 1);
    }

    private void selectRobot(int playerIndex, int robotIndex) {
        if (playerIndex < players.size()) {
            Player p = players.get(playerIndex);
            if (robotIndex < p.getRobots().size()) {
                currentPlayerIndex = playerIndex;
                currentRobotIndex = robotIndex;

                Robot r = getCurrentRobot();
                if (r.getHealth() <= 0) {
                    System.out.println("این ربات مرده است.");
                    return;
                }
                isPlayerTurnActive = true;
                shotFired = false;
                updateUI();
                renderMap();
                mapGrid.requestFocus();

                System.out.println("ربات انتخاب شد: " + r.getName());
            }
        }
    }

    @FXML private void reload() { reloadAmmo(); }
    @FXML private void exitGame() { Platform.exit(); }

    // Added the missing method that your FXML is looking for
    @FXML
    private void exitToMainMenu() {
        // For now, this just exits the game like exitGame()
        // Later you can implement proper main menu navigation here
        Platform.exit();
    }
}