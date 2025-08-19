package game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GameController {

    // FXML components
    @FXML
    private VBox topVBox;
    @FXML
    private HBox ammoHBox;
    @FXML
    private Label ammoLabel;
    @FXML
    private Label activeAmmoLabel;
    @FXML
    private VBox healthVBox;
    @FXML
    private HBox healthRow1;
    @FXML
    private HBox healthRow2;
    @FXML
    private HBox healthRow3;
    @FXML
    private HBox healthRow4;
    @FXML
    private ProgressBar health1;
    @FXML
    private ProgressBar health2;
    @FXML
    private ProgressBar health3;
    @FXML
    private ProgressBar health4;
    @FXML
    private GridPane mapGrid;
    @FXML
    private Button selectRobot1Button;
    @FXML
    private Button selectRobot2Button;
    @FXML
    private Button selectRobot3Button;
    @FXML
    private Button selectRobot4Button;
    @FXML
    private Button moveLeftButton;
    @FXML
    private Button moveUpButton;
    @FXML
    private Button moveDownButton;
    @FXML
    private Button moveRightButton;
    @FXML
    private Button shootButton;

    // New: For matrix debug scroller
    @FXML
    private TextArea debugTextArea;

    private int mapHeight = 12;
    private int mapWidth = 12;
    private Cell[][] map;

    private List<Player> players = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private int currentRobotIndex = 0;
    private int globalTurnIndex = 0;

    private final List<int[]> turnOrder = Arrays.asList(
            new int[]{0, 0}, // R1
            new int[]{0, 1}, // R2
            new int[]{1, 0}, // R3
            new int[]{1, 1}  // R4
    );

    private boolean isManualSelectionMode = false;
    private int manuallySelectedPlayerIndex = 0;
    private int manuallySelectedRobotIndex = 0;

    private boolean shotFired = false;
    private boolean isPlayerTurnActive = false;
    private List<AIPlayer> aiPlayers = new ArrayList<>();

    private BattleMode battleMode = BattleMode.PLAYER_VS_PLAYER;

    private int currentRow = 0;
    private int currentCol = 0;

    private RenderFixer renderFixer;
    private Random random = new Random();

    private boolean isMoving = false;

    // Getters and Setters
    public Cell[][] getMap() {
        return map;
    }

    public void setMapDimensions(int width, int height) {
        this.mapWidth = width;
        this.mapHeight = height;
        this.map = new Cell[mapHeight][mapWidth];
        initMap();
    }

    public void setMap(Cell[][] map) {
        this.map = map;
        this.mapHeight = map.length;
        this.mapWidth = map[0].length;
    }

    public int getBoardWidth() {
        return mapWidth;
    }

    public int getBoardHeight() {
        return mapHeight;
    }

    public void setRenderFixer(RenderFixer renderFixer) {
        this.renderFixer = renderFixer;
    }

    public RenderFixer getRenderFixer() {
        return renderFixer;
    }

    public void setGameMode(BattleMode mode) {
        this.battleMode = mode;
        System.out.println("Game mode set to: " + mode);
    }

    private Robot getSelectedRobot() {
        if (isManualSelectionMode) {
            if (manuallySelectedPlayerIndex < players.size() &&
                    manuallySelectedRobotIndex < players.get(manuallySelectedPlayerIndex).getRobots().size()) {
                return players.get(manuallySelectedPlayerIndex).getRobots().get(manuallySelectedRobotIndex);
            }
        } else {
            return getCurrentRobot();
        }
        return null;
    }

    // FXML event handlers
    @FXML
    public void selectRobot1(ActionEvent event) {
        selectRobot(0, 0);
        activeAmmoLabel.setText("Robot 1 selected");
        isManualSelectionMode = true;
        isPlayerTurnActive = true;
        updateUI();
    }

    @FXML
    public void selectRobot2(ActionEvent event) {
        selectRobot(0, 1);
        activeAmmoLabel.setText("Robot 2 selected");
        isManualSelectionMode = true;
        isPlayerTurnActive = true;
        updateUI();
    }

    @FXML
    public void selectRobot3(ActionEvent event) {
        selectRobot(1, 0);
        activeAmmoLabel.setText("Robot 3 selected");
        isManualSelectionMode = true;
        isPlayerTurnActive = true;
        updateUI();
    }

    @FXML
    public void selectRobot4(ActionEvent event) {
        selectRobot(1, 1);
        activeAmmoLabel.setText("Robot 4 selected");
        isManualSelectionMode = true;
        isPlayerTurnActive = true;
        updateUI();
    }

    @FXML
    public void moveRobotLeft(ActionEvent event) {
        moveSelectedRobot(-1, 0, Direction.LEFT);
        updateUI();
        // Removed nextTurn() to allow unlimited moves until shoot
    }

    @FXML
    public void moveRobotUp(ActionEvent event) {
        moveSelectedRobot(0, -1, Direction.UP);
        updateUI();
        // Removed nextTurn() to allow unlimited moves until shoot
    }

    @FXML
    public void moveRobotDown(ActionEvent event) {
        moveSelectedRobot(0, 1, Direction.DOWN);
        updateUI();
        // Removed nextTurn() to allow unlimited moves until shoot
    }

    @FXML
    public void moveRobotRight(ActionEvent event) {
        moveSelectedRobot(1, 0, Direction.RIGHT);
        updateUI();
        // Removed nextTurn() to allow unlimited moves until shoot
    }

    @FXML
    public void shoot(ActionEvent event) {
        shootSelectedRobot();
        // Keep nextTurn() here, as turn ends after shoot
    }

    @FXML
    public void handleExit(ActionEvent event) {
        Platform.exit();
        System.out.println("Game exited");
    }

    public void selectRobot(int playerIndex, int robotIndex) {
        if (playerIndex < players.size() &&
                robotIndex < players.get(playerIndex).getRobots().size()) {
            isManualSelectionMode = true;
            manuallySelectedPlayerIndex = playerIndex;
            manuallySelectedRobotIndex = robotIndex;
            System.out.println("Manually selected robot: " +
                    players.get(playerIndex).getRobots().get(robotIndex).getName() +
                    ", isPlayerTurnActive=" + isPlayerTurnActive);
        }
    }

    public void disableManualSelection() {
        isManualSelectionMode = false;
        System.out.println("Manual selection disabled, using turn order");
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing game...");
        if (map == null) {
            map = new Cell[mapHeight][mapWidth];
        }

        try {
            battleMode = Settings.getBattleMode();
            if (battleMode == null) {
                System.out.println("Warning: Settings.getBattleMode() returned null, using default");
                battleMode = BattleMode.PLAYER_VS_PLAYER;
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load battle mode from Settings, using default: " + e.getMessage());
            battleMode = BattleMode.PLAYER_VS_PLAYER;
        }

        initMap();
        initPlayersAndRobots();
        updateUI();
        System.out.println("Game initialized with mode: " + battleMode);
    }

    public void startGame() {
        initAIPlayers();
        isPlayerTurnActive = (battleMode != BattleMode.AI_VS_AI);
        System.out.println("Game started, isPlayerTurnActive=" + isPlayerTurnActive);
        startTurnForCurrentRobot();
        updateUI();
    }

    private void initMap() {
        for (int r = 0; r < mapHeight; r++) {
            for (int c = 0; c < mapWidth; c++) {
                map[r][c] = new Cell(r, c);
            }
        }
        generateRandomObstacles();
        updateUI();
    }

    private void generateRandomObstacles() {
        List<Point> safeZones = new ArrayList<>();
        safeZones.add(new Point(0, 0)); // R1
        safeZones.add(new Point(0, 1)); // R2
        safeZones.add(new Point(11, 11)); // R3
        safeZones.add(new Point(11, 10)); // R4

        List<Point> expandedSafeZones = new ArrayList<>();
        for (Point p : safeZones) {
            for (int dr = -2; dr <= 2; dr++) {
                for (int dc = -2; dc <= 2; dc++) {
                    int newR = p.x + dr;
                    int newC = p.y + dc;
                    if (isInBounds(newR, newC)) {
                        expandedSafeZones.add(new Point(newR, newC));
                    }
                }
            }
        }
        safeZones = expandedSafeZones;

        generateMazeWalls(safeZones);
        generateMazeMines(safeZones);
        System.out.println("Generated maze-like obstacle arrangement");
    }

    private void generateMazeWalls(List<Point> safeZones) {
        createHorizontalCorridors(safeZones);
        createVerticalCorridors(safeZones);
        createMazeRooms(safeZones);
        createConnectingPassages(safeZones);
    }

    private void createHorizontalCorridors(List<Point> safeZones) {
        int[] corridorRows = {3, 6, 9};
        for (int row : corridorRows) {
            for (int col = 1; col < mapWidth - 1; col++) {
                if (!isInSafeZone(row, col, safeZones) && map[row][col].isEmpty()) {
                    if (random.nextDouble() < 0.3) {
                        int wallType = random.nextInt(3);
                        createWall(row, col, wallType);
                    }
                }
            }
        }
        System.out.println("Created horizontal corridors");
    }

    private void createVerticalCorridors(List<Point> safeZones) {
        int[] corridorCols = {3, 6, 9};
        for (int col : corridorCols) {
            for (int row = 1; row < mapHeight - 1; row++) {
                if (!isInSafeZone(row, col, safeZones) && map[row][col].isEmpty()) {
                    if (random.nextDouble() < 0.3) {
                        int wallType = random.nextInt(3);
                        createWall(row, col, wallType);
                    }
                }
            }
        }
        System.out.println("Created vertical corridors");
    }

    private void createMazeRooms(List<Point> safeZones) {
        int[][] roomCenters = {{2, 2}, {2, 9}, {9, 2}, {9, 9}, {5, 5}};
        for (int[] center : roomCenters) {
            int centerRow = center[0];
            int centerCol = center[1];
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int r = centerRow + dr;
                    int c = centerCol + dc;
                    if (isInBounds(r, c) && !isInSafeZone(r, c, safeZones) && map[r][c].isEmpty()) {
                        if (!(dr == 0 && dc == 0) && random.nextDouble() < 0.6) {
                            int wallType = random.nextInt(3);
                            createWall(r, c, wallType);
                        }
                    }
                }
            }
        }
        System.out.println("Created maze rooms");
    }

    private void createConnectingPassages(List<Point> safeZones) {
        for (int i = 0; i < 15; i++) {
            int row = random.nextInt(mapHeight);
            int col = random.nextInt(mapWidth);
            if (!isInSafeZone(row, col, safeZones) && map[row][col].isEmpty()) {
                int wallType = random.nextInt(3);
                createWall(row, col, wallType);
            }
        }
        System.out.println("Created connecting passages");
    }

    private void generateMazeMines(List<Point> safeZones) {
        int[][] minePositions = {
                {2, 5}, {5, 3}, {7, 4}, {3, 10},
                {8, 7}, {10, 3}, {5, 11}, {1, 10},
                {11, 2}, {7, 11}
        };
        for (int[] pos : minePositions) {
            int r = pos[0];
            int c = pos[1];
            if (!isInSafeZone(r, c, safeZones) && map[r][c].isEmpty()) {
                map[r][c].setEntity(new StandardMine(r, c));
                System.out.println("Added Mine at (" + r + "," + c + ")");
            }
        }
    }

    private boolean isInSafeZone(int r, int c, List<Point> safeZones) {
        for (Point safe : safeZones) {
            if (safe.x == r && safe.y == c) return true;
        }
        return false;
    }

    private void createWall(int r, int c, int wallType) {
        switch (wallType) {
            case 0:
                map[r][c].setEntity(new NormalWall(r, c));
                System.out.println("Added Normal Wall at (" + r + "," + c + ")");
                break;
            case 1:
                map[r][c].setEntity(new SteelWall(r, c));
                System.out.println("Added Steel Wall at (" + r + "," + c + ")");
                break;
            case 2:
                map[r][c].setEntity(new WoodenWall(r, c));
                System.out.println("Added Wooden Wall at (" + r + "," + c + ")");
                break;
        }
    }

    public static class SteelWall extends Obstacle {
        private int hitCount = 0;

        public SteelWall(int x, int y) {
            super(x, y, ObstacleType.STEEL_WALL);
        }

        @Override
        public void applyEffect(Robot robot) {
            hitCount++;
            if (hitCount >= 3) {
                System.out.println("Steel Wall at (" + getX() + ", " + getY() + ") has been destroyed!");
            } else {
                System.out.println("Steel Wall at (" + getX() + ", " + getY() + ") has been hit! Total hits: " + hitCount);
            }
        }

        public void hit() {
            hitCount++;
            if (hitCount >= 3) {
                System.out.println("Steel Wall at (" + getX() + ", " + getY() + ") is destroyed after 3 hits!");
            }
        }

        public boolean isDestroyed() {
            return hitCount >= 3;
        }

        public int getHitCount() {
            return hitCount;
        }
    }

    public static class WoodenWall extends Obstacle {
        private int hitCount = 0;

        public WoodenWall(int x, int y) {
            super(x, y, ObstacleType.WOODEN_WALL);
        }

        @Override
        public void applyEffect(Robot robot) {
            hitCount++;
            if (hitCount >= 1) {
                System.out.println("Wooden Wall at (" + getX() + ", " + getY() + ") has been destroyed!");
            } else {
                System.out.println("Wooden Wall at (" + getX() + ", " + getY() + ") has been hit! Total hits: " + hitCount);
            }
        }

        public void hit() {
            hitCount++;
            if (hitCount >= 1) {
                System.out.println("Wooden Wall at (" + getX() + ", " + getY() + ") is destroyed after 1 hit!");
            }
        }

        public boolean isDestroyed() {
            return hitCount >= 1;
        }

        public int getHitCount() {
            return hitCount;
        }
    }

    private void initPlayersAndRobots() {
        players.clear();
        Player p1, p2;

        List<Robot> robots1 = new ArrayList<>();
        List<Robot> robots2 = new ArrayList<>();

        switch (battleMode) {
            case PLAYER_VS_PLAYER:
                p1 = new Player("Player1", false, robots1);
                p2 = new Player("Player2", false, robots2);
                System.out.println("Game mode: PvP");
                break;
            case PLAYER_VS_AI:
                p1 = new Player("Player1", false, robots1);
                p2 = new Player("Player2", true, robots2);
                System.out.println("Game mode: PvAI");
                break;
            case AI_VS_AI:
                p1 = new Player("Player1", true, robots1);
                p2 = new Player("Player2", true, robots2);
                System.out.println("Game mode: AI vs AI");
                break;
            default:
                p1 = new Player("Player1", false, robots1);
                p2 = new Player("Player2", false, robots2);
                System.out.println("Game mode: PvP (default)");
                break;
        }

        Robot r1 = new Robot("R1", 0, 0, "Red", Direction.RIGHT);
        Robot r2 = new Robot("R2", 0, 1, "Red", Direction.RIGHT);
        Robot r3 = new Robot("R3", 11, 11, "Blue", Direction.LEFT);
        Robot r4 = new Robot("R4", 11, 10, "Blue", Direction.LEFT);

        setRobotAttributes(r1);
        setRobotAttributes(r2);
        setRobotAttributes(r3);
        setRobotAttributes(r4);

        p1.addRobot(r1);
        p1.addRobot(r2);
        p2.addRobot(r3);
        p2.addRobot(r4);

        map[r1.getRow()][r1.getCol()].setEntity(r1);
        map[r2.getRow()][r2.getCol()].setEntity(r2);
        map[r3.getRow()][r3.getCol()].setEntity(r3);
        map[r4.getRow()][r4.getCol()].setEntity(r4);

        printRobotPosition(r1);
        printRobotPosition(r2);
        printRobotPosition(r3);
        printRobotPosition(r4);
        System.out.println("Player1 robots: " + p1.getRobots().size());
        System.out.println("Player2 robots: " + p2.getRobots().size());

        players.add(p1);
        players.add(p2);

        System.out.println("Game initialized with mode: " + battleMode);
    }

    private void setRobotAttributes(Robot robot) {
        robot.setAmmo(10);
        robot.setRange(3);
        robot.setDamage(20);
        robot.setBarrelLength(20);
        robot.setHealth(100);
    }

    private void initAIPlayers() {
        aiPlayers.clear();
        boolean isAIvsAI = (battleMode == BattleMode.AI_VS_AI);
        for (Player p : players) {
            if (p.isAI()) {
                for (Robot r : p.getRobots()) {
                    AIPlayer ai = new AIPlayer(r, AIPlayer.AILevel.MEDIUM, isAIvsAI, r.getName());
                    aiPlayers.add(ai);
                }
            }
        }
        System.out.println("AI Players initialized: " + aiPlayers.size());
    }

    public void handleKeyPressed(String key) {
        if (shotFired || !isPlayerTurnActive) {
            System.out.println("Key ignored: shotFired=" + shotFired + ", isPlayerTurnActive=" + isPlayerTurnActive);
            return;
        }
        Robot selectedRobot = getSelectedRobot();
        if (selectedRobot == null) {
            System.out.println("Key ignored: No selected robot");
            return;
        }
        Player robotOwner = null;
        for (Player p : players) {
            if (p.getRobots().contains(selectedRobot)) {
                robotOwner = p;
                break;
            }
        }
        if (robotOwner == null || robotOwner.isAI()) {
            System.out.println("Key ignored: Robot owner is AI or not found");
            return;
        }
        System.out.println("Key pressed: " + key + " for robot: " + selectedRobot.getName());
        boolean isRedRobot = selectedRobot.getName().equals("R1") || selectedRobot.getName().equals("R2");
        if (isRedRobot) {
            switch (key.toUpperCase()) {
                case "W" -> {
                    moveSelectedRobot(0, -1, Direction.UP);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "S" -> {
                    moveSelectedRobot(0, 1, Direction.DOWN);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "A" -> {
                    moveSelectedRobot(-1, 0, Direction.LEFT);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "D" -> {
                    moveSelectedRobot(1, 0, Direction.RIGHT);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "C", "SPACE" -> {
                    shootSelectedRobot();
                    // Keep nextTurn() here
                }
                case "R" -> {
                    reloadSelectedRobot();
                    // Removed nextTurn() assuming reload doesn't end turn
                }
                default -> System.out.println("Unhandled key for red robot: " + key);
            }
        } else {
            switch (key.toUpperCase()) {
                case "W", "UP" -> {
                    moveSelectedRobot(0, -1, Direction.UP);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "A", "LEFT" -> {
                    moveSelectedRobot(-1, 0, Direction.LEFT);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "S", "DOWN" -> {
                    moveSelectedRobot(0, 1, Direction.DOWN);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "D", "RIGHT" -> {
                    moveSelectedRobot(1, 0, Direction.RIGHT);
                    // Removed nextTurn() to allow unlimited moves until shoot
                }
                case "C", "SPACE" -> {
                    shootSelectedRobot();
                    // Keep nextTurn() here
                }
                case "R" -> {
                    reloadSelectedRobot();
                    // Removed nextTurn() assuming reload doesn't end turn
                }
                default -> System.out.println("Unhandled key for blue robot: " + key);
            }
        }
        updateUI();
    }

    private void moveSelectedRobot(int dx, int dy, Direction direction) {
        Robot robot = getSelectedRobot();
        if (robot == null || robot.getHealth() <= 0) {
            System.out.println("Cannot move: Robot is null or dead");
            return;
        }
        int newRow = robot.getRow() + dy;
        int newCol = robot.getCol() + dx;
        if (isInBounds(newRow, newCol) && map[newRow][newCol].isEmpty()) {
            map[robot.getRow()][robot.getCol()].removeEntity();
            robot.setPosition(newRow, newCol);
            robot.setDirection(direction);
            map[newRow][newCol].setEntity(robot);
            printRobotPosition(robot);
        } else {
            System.out.println("Movement blocked for " + robot.getName() + " at (" + newRow + "," + newCol + ")");
            handleCollision(robot, newRow, newCol);
        }
    }

    private void shootSelectedRobot() {
        Robot robot = getSelectedRobot();
        if (robot == null || robot.getHealth() <= 0) {
            System.out.println("Cannot shoot: Robot is null or dead");
            return;
        }
        setCurrentRobotForShooting(robot);
        shoot();
        if (!players.get(manuallySelectedPlayerIndex).isAI()) {
            nextTurn();
        }
    }

    private void setCurrentRobotForShooting(Robot robot) {
        for (int pIndex = 0; pIndex < players.size(); pIndex++) {
            Player p = players.get(pIndex);
            for (int rIndex = 0; rIndex < p.getRobots().size(); rIndex++) {
                if (p.getRobots().get(rIndex) == robot) {
                    currentPlayerIndex = pIndex;
                    currentRobotIndex = rIndex;
                    return;
                }
            }
        }
    }

    private void reloadSelectedRobot() {
        Robot robot = getSelectedRobot();
        if (robot == null || robot.getHealth() <= 0) {
            System.out.println("Cannot reload: Robot is null or dead");
            return;
        }
        robot.setAmmo(10);
        robot.resetDamage();
        System.out.println("ربات " + robot.getName() + ": مهمات پر شد");
        updateUI();
    }

    public void handleCollision(Robot robot, int newRow, int newCol) {
        for (Player p : players) {
            for (Robot r : p.getRobots()) {
                if (r != robot && r.getRow() == newRow && r.getCol() == newCol) {
                    System.out.println("ربات " + robot.getName() + " با ربات " + r.getName() + " برخورد کرد!");
                    return;
                }
            }
        }
        if (isInBounds(newRow, newCol) && map[newRow][newCol].getEntity() instanceof Obstacle) {
            Obstacle obstacle = (Obstacle) map[newRow][newCol].getEntity();
            System.out.println("ربات " + robot.getName() + " با مانع " + obstacle.getName() + " برخورد کرد!");
            obstacle.applyEffect(robot);
            updateUI();
        }
    }

    public Robot getCurrentRobot() {
        if (players.isEmpty() || currentPlayerIndex >= players.size() ||
                players.get(currentPlayerIndex).getRobots().isEmpty() ||
                currentRobotIndex >= players.get(currentPlayerIndex).getRobots().size()) {
            System.out.println("No current robot: players=" + players.size() + ", currentPlayerIndex=" + currentPlayerIndex + ", currentRobotIndex=" + currentRobotIndex);
            return null;
        }
        Robot robot = players.get(currentPlayerIndex).getRobots().get(currentRobotIndex);
        System.out.println("Current robot: " + robot.getName());
        return robot;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < mapHeight && col >= 0 && col < mapWidth;
    }

    private void nextTurn() {
        shotFired = false;
        globalTurnIndex++;
        int[] nextIndices = turnOrder.get(globalTurnIndex % turnOrder.size());
        currentPlayerIndex = nextIndices[0];
        currentRobotIndex = nextIndices[1];
        Robot robot = getCurrentRobot();
        if (robot == null || robot.getHealth() <= 0) {
            System.out.println("Skipping turn: Robot is null or dead");
            nextTurn();
            return;
        }
        checkGameEnd();
        startTurnForCurrentRobot();
        updateUI();
    }

    private void startTurnForCurrentRobot() {
        Robot robot = getCurrentRobot();
        if (robot == null || robot.getHealth() <= 0) {
            System.out.println("ربات " + (robot != null ? robot.getName() : "نامشخص") + " نابود شده، نوبت بعدی");
            nextTurn();
            return;
        }
        System.out.println("Starting turn for robot: " + robot.getName() + ", Player: " + players.get(currentPlayerIndex).getName());
        if (players.get(currentPlayerIndex).isAI()) {
            isPlayerTurnActive = false;
            System.out.println("AI turn for " + robot.getName() + ", isPlayerTurnActive=false");
            performAITurn();
        } else {
            isPlayerTurnActive = true;
            System.out.println("Player turn started for " + robot.getName() + ", isPlayerTurnActive=true");
        }
        updateUI();
    }

    private void performAITurn() {
        Robot currentRobot = getCurrentRobot();
        if (currentRobot == null) {
            System.out.println("خطا: ربات فعلی پیدا نشد!");
            nextTurn();
            return;
        }
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
                nextTurn();
            }
            case ATTACK -> {
                Point targetPoint = (Point) action.getTarget();
                if (targetPoint != null) {
                    Direction dir = calculateDirection(
                            targetPoint.x - currentRobot.getCol(),
                            targetPoint.y - currentRobot.getRow()
                    );
                    currentRobot.setDirection(dir);
                    shoot();
                    nextTurn();
                } else {
                    System.out.println("هدف برای ربات " + currentRobot.getName() + " پیدا نشد");
                    nextTurn();
                }
            }
            case WAIT -> {
                System.out.println("ربات " + currentRobot.getName() + ": منتظر ماند");
                nextTurn();
            }
        }
        updateUI();
    }

    private void moveRobotAI(int dx, int dy, Direction direction) {
        Robot robot = getCurrentRobot();
        if (robot == null) return;
        int newRow = robot.getRow() + dy;
        int newCol = robot.getCol() + dx;
        if (isInBounds(newRow, newCol) && map[newRow][newCol].isEmpty()) {
            map[robot.getRow()][robot.getCol()].removeEntity();
            robot.setPosition(newRow, newCol);
            robot.setDirection(direction);
            map[newRow][newCol].setEntity(robot);
            printRobotPosition(robot);
        } else {
            System.out.println("ربات " + robot.getName() + ": حرکت غیرمجاز");
        }
        updateUI();
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
        boolean isAIvsAI = (battleMode == BattleMode.AI_VS_AI);
        for (Player p : players) {
            if (p.isAI()) {
                for (Robot r : p.getRobots()) {
                    AIPlayer ai = findAIPlayerByName(r.getName());
                    if (ai != null) {
                        currentAIPlayers.add(ai);
                    } else {
                        currentAIPlayers.add(new AIPlayer(r, AIPlayer.AILevel.MEDIUM, isAIvsAI, r.getName()));
                    }
                }
            }
        }
        return GameState.fromCurrentState(currentAIPlayers, map, mapWidth, mapHeight);
    }

    private void shoot() {
        if (shotFired || !isPlayerTurnActive) {
            System.out.println("Shoot ignored: shotFired=" + shotFired + ", isPlayerTurnActive=" + isPlayerTurnActive);
            return;
        }
        Robot shooter = getCurrentRobot();
        if (shooter == null) {
            System.out.println("Shoot failed: No current robot");
            shotFired = false;
            isPlayerTurnActive = false;
            nextTurn();
            return;
        }
        System.out.println("شلیک توسط " + shooter.getName() + " - جهت: " + shooter.getDirection() + " - قدرت آسیب: " + shooter.getCurrentDamage());
        if (shooter.getAmmo() == 0) {
            System.out.println("مهمات تمام است!");
            shotFired = false;
            isPlayerTurnActive = false;
            nextTurn();
            return;
        } else if (shooter.getAmmo() != Integer.MAX_VALUE) {
            shooter.decreaseAmmo();
        }
        int bulletRow = shooter.getRow();
        int bulletCol = shooter.getCol();
        final int dRow, dCol;
        if (shooter.getDirection() == null) {
            System.out.println("⚠️ جهت ربات تنظیم نشده است! جهت پیش‌فرض: RIGHT");
            shooter.setDirection(Direction.RIGHT);
            System.out.println("⚠️ جهت ربات " + shooter.getName() + " تنظیم شد: RIGHT");
        }
        switch (shooter.getDirection()) {
            case UP -> { dRow = -1; dCol = 0; }
            case DOWN -> { dRow = 1; dCol = 0; }
            case LEFT -> { dRow = 0; dCol = -1; }
            case RIGHT -> { dRow = 0; dCol = 1; }
            default -> { dRow = 0; dCol = 1; }
        }
        shotFired = true;
        int steps = 0;
        int maxRange = shooter.getRange();
        Timeline bulletAnimation = new Timeline();
        Platform.runLater(this::updateMapGrid);
        while (steps <= maxRange && isInBounds(bulletRow, bulletCol)) {
            bulletRow += dRow;
            bulletCol += dCol;
            steps++;
            int finalBulletRow = bulletRow;
            int finalBulletCol = bulletCol;
            System.out.println("موقعیت تیر: (" + finalBulletRow + ", " + finalBulletCol + ")");
            if (!isInBounds(finalBulletRow, finalBulletCol) || steps > maxRange) {
                bulletAnimation.getKeyFrames().add(
                        new KeyFrame(Duration.millis(300 * steps), e -> {
                            shotFired = false;
                            isPlayerTurnActive = false;
                            System.out.println("شلیک ربات " + shooter.getName() + " پایان یافت");
                            updateUI();
                            nextTurn();
                        })
                );
                break;
            }
            Cell target = map[finalBulletRow][finalBulletCol];
            Entity entity = target.getEntity();
            bulletAnimation.getKeyFrames().add(
                    new KeyFrame(Duration.millis(300 * steps), e -> {
                        Rectangle bullet = new Rectangle(20, 20, Color.YELLOW);
                        mapGrid.add(bullet, finalBulletCol, finalBulletRow);
                        new Timeline(new KeyFrame(Duration.millis(250), ev -> mapGrid.getChildren().remove(bullet))).play();
                    })
            );
            if (entity instanceof Obstacle obstacle) {
                bulletAnimation.getKeyFrames().add(
                        new KeyFrame(Duration.millis(300 * steps), e -> {
                            obstacle.applyEffect(shooter);
                            if (obstacle instanceof SteelWall steelWall && steelWall.isDestroyed() ||
                                    obstacle instanceof WoodenWall woodenWall && woodenWall.isDestroyed()) {
                                target.removeEntity();
                                System.out.println(obstacle.getName() + " at (" + finalBulletRow + ", " + finalBulletCol + ") removed from map");
                            }
                            shotFired = false;
                            isPlayerTurnActive = false;
                            System.out.println("شلیک ربات " + shooter.getName() + " به مانع " + obstacle.getName() + " برخورد کرد");
                            updateUI();
                            nextTurn();
                        })
                );
                break;
            }
            if (entity instanceof Robot hit) {
                bulletAnimation.getKeyFrames().add(
                        new KeyFrame(Duration.millis(300 * steps), e -> {
                            int currentDamage = shooter.getCurrentDamage();
                            hit.takeDamage(currentDamage);
                            System.out.println("💥 " + shooter.getName() + " hit " + hit.getName() + " for " + currentDamage + " damage!");
                            shooter.reduceDamageBy5Percent();
                            System.out.println("🔽 " + shooter.getName() + "'s damage reduced to " + shooter.getCurrentDamage());
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
                            shotFired = false;
                            isPlayerTurnActive = false;
                            updateUI();
                            nextTurn();
                        })
                );
                break;
            }
        }
        bulletAnimation.play();
    }

    private void checkGameEnd() {
        Player winner = null;
        boolean player1HasRobots = false;
        boolean player2HasRobots = false;

        for (Robot r : players.get(0).getRobots()) {
            if (r.isAlive()) {
                player1HasRobots = true;
                break;
            }
        }
        for (Robot r : players.get(1).getRobots()) {
            if (r.isAlive()) {
                player2HasRobots = true;
                break;
            }
        }

        if (!player1HasRobots && !player2HasRobots) {
            endGame("All robots are destroyed! It's a draw!");
        } else if (!player1HasRobots) {
            winner = players.get(1);
            endGame(winner.getName() + " (Blue Team) wins!");
        } else if (!player2HasRobots) {
            winner = players.get(0);
            endGame(winner.getName() + " (Red Team) wins!");
        }
    }

    private void endGame(String message) {
        System.out.println("Game Over: " + message);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            Stage stage = (Stage) mapGrid.getScene().getWindow();
            stage.close();
        });
    }

    public void exitGame() {
        System.out.println("Exiting the Game");
        Platform.exit();
    }

    private void updateUI() {
        Platform.runLater(() -> {
            updateHealthBars();
            Robot selectedRobot = getSelectedRobot();
            if (selectedRobot != null) {
                ammoLabel.setText("Ammo: " + selectedRobot.getAmmo());
            } else {
                ammoLabel.setText("Ammo: 0");
            }
            updateMapGrid();
            updateDebugMatrix();
        });
    }

    private void updateHealthBars() {
        Robot r1 = findRobotByName("R1");
        Robot r2 = findRobotByName("R2");
        Robot r3 = findRobotByName("R3");
        Robot r4 = findRobotByName("R4");

        health1.setProgress(r1 != null && r1.isAlive() ? r1.getHealth() / 100.0 : 0.0);
        health2.setProgress(r2 != null && r2.isAlive() ? r2.getHealth() / 100.0 : 0.0);
        health3.setProgress(r3 != null && r3.isAlive() ? r3.getHealth() / 100.0 : 0.0);
        health4.setProgress(r4 != null && r4.isAlive() ? r4.getHealth() / 100.0 : 0.0);
    }

    private void updateMapGrid() {
        mapGrid.getChildren().clear();
        for (int r = 0; r < mapHeight; r++) {
            for (int c = 0; c < mapWidth; c++) {
                Rectangle cell = new Rectangle(30, 30);
                Entity entity = map[r][c].getEntity();
                if (entity instanceof Robot robot) {
                    if (robot.getName().equals("R1") || robot.getName().equals("R2")) {
                        cell.setFill(Color.RED);
                    } else if (robot.getName().equals("R3") || robot.getName().equals("R4")) {
                        cell.setFill(Color.BLUE);
                    } else {
                        System.out.println("Warning: Unknown robot name " + robot.getName());
                        cell.setFill(Color.GRAY);
                    }
                } else if (entity instanceof NormalWall) {
                    cell.setFill(Color.GRAY);
                } else if (entity instanceof SteelWall) {
                    cell.setFill(Color.DARKGRAY);
                } else if (entity instanceof WoodenWall) {
                    cell.setFill(Color.BROWN);
                } else if (entity instanceof StandardMine) {
                    cell.setFill(Color.BLACK);
                } else {
                    cell.setFill(Color.LIGHTGRAY);
                }
                mapGrid.add(cell, c, r);
            }
        }
    }

    private void updateDebugMatrix() {
        if (debugTextArea == null) {
            return;
        }
        StringBuilder matrixStr = new StringBuilder();
        matrixStr.append("Map Matrix (Rows: ").append(mapHeight).append(", Cols: ").append(mapWidth).append("):\n");
        for (int r = 0; r < mapHeight; r++) {
            for (int c = 0; c < mapWidth; c++) {
                Entity entity = map[r][c].getEntity();
                char symbol;
                if (entity instanceof Robot robot) {
                    if (robot.getName().startsWith("R1") || robot.getName().startsWith("R2")) {
                        symbol = 'R';
                    } else {
                        symbol = 'B';
                    }
                } else if (entity instanceof NormalWall) {
                    symbol = 'N';
                } else if (entity instanceof SteelWall) {
                    symbol = 'S';
                } else if (entity instanceof WoodenWall) {
                    symbol = 'W';
                } else if (entity instanceof StandardMine) {
                    symbol = 'M';
                } else {
                    symbol = '.';
                }
                matrixStr.append(symbol).append(" ");
            }
            matrixStr.append("\n");
        }
        debugTextArea.setText(matrixStr.toString());
    }

    private void printRobotPosition(Robot robot) {
        String color = robot.getColor().equals("Red") ? "قرمز" : "آبی";
        System.out.println("ربات " + color + "(" + robot.getRow() + "," + robot.getCol() + ")");
    }
}

