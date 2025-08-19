package game;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.Parent;
import java.io.IOException;

public class GameUI extends Application {
    private Board board;
    private Player player1;  // بازیکن اول
    private Player player2;  // بازیکن دوم
    private GameManager gameManager;
    private Stage primaryStage;
    private GameController gameController;

    public GameUI(Board board, Player player1, Player player2, GameManager gameManager) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.gameManager = gameManager;
    }

    // Default constructor for JavaFX Application
    public GameUI() {
        // Initialize with defaults if needed
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // صفحه خوش‌آمدگویی
        VBox welcomeLayout = new VBox(20);
        welcomeLayout.setAlignment(Pos.CENTER);
        welcomeLayout.setPadding(new Insets(20));

        LinearGradient backgroundFill = new LinearGradient(0, 0, 1, 1, true, null,
                new Stop(0, Color.web("#FFF5E1")), // کرمی
                new Stop(1, Color.web("#D2B48C"))); // قهوه‌ای روشن
        welcomeLayout.setBackground(new Background(new BackgroundFill(backgroundFill, CornerRadii.EMPTY, Insets.EMPTY)));

        Label welcomeText = new Label("خوش آمدید :)");
        welcomeText.setStyle("-fx-font-size: 36px; -fx-text-fill: black; -fx-highlight-fill: #D8BFD8;"); // هایلایت بنفش کم‌رنگ
        welcomeText.setAlignment(Pos.CENTER);

        Button startButton = new Button("شروع");
        startButton.setStyle("-fx-background-color: #00FF00; -fx-text-fill: white; -fx-font-size: 24px;");
        startButton.setOnAction(e -> showGameScreen());

        Button settingsButton = new Button("تنظیمات");
        settingsButton.setStyle("-fx-background-color: #ADD8E6; -fx-text-fill: black; -fx-font-size: 24px;");
        settingsButton.setOnAction(e -> showSettingsScreen());

        welcomeLayout.getChildren().addAll(welcomeText, startButton, settingsButton);
        Scene welcomeScene = new Scene(welcomeLayout, 400, 300);
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Battle Robots");
        primaryStage.show();
    }

    private void showGameScreen() {
        try {
            // Try to load Game.fxml first (based on your error message)
            FXMLLoader loader = null;
            Parent gameRoot = null;

            try {
                loader = new FXMLLoader(getClass().getResource("/game/Game.fxml"));
                gameRoot = loader.load();
            } catch (IOException e) {
                // If Game.fxml doesn't exist, try GameController.fxml
                System.out.println("Game.fxml not found, trying GameController.fxml");
                loader = new FXMLLoader(getClass().getResource("/game/GameController.fxml"));
                gameRoot = loader.load();
            }

            Scene gameScene = new Scene(gameRoot, 800, 600);
            primaryStage.setScene(gameScene);

            // Get the controller and initialize
            Object controller = loader.getController();

            // Handle different controller types
            if (controller instanceof GameController) {
                gameController = (GameController) controller;
                gameController.initialize();
                gameController.startGame();
            } else {
                // For other controller types, try to call common methods
                try {
                    // Try to set primary stage if the method exists
                    try {
                        controller.getClass().getMethod("setPrimaryStage", Stage.class).invoke(controller, primaryStage);
                    } catch (Exception ex) {
                        System.out.println("Controller doesn't have setPrimaryStage method");
                    }

                    // Try to call initialize method if it exists
                    try {
                        controller.getClass().getMethod("initialize").invoke(controller);
                    } catch (Exception ex) {
                        System.out.println("Controller doesn't have initialize method");
                    }

                    // If it's not a GameController, create our own
                    if (gameController == null) {
                        gameController = new GameController();
                        gameController.initialize();
                        gameController.startGame();
                    }

                } catch (Exception ex) {
                    System.out.println("Error initializing controller: " + ex.getMessage());
                }
            }

            // Set focus for key events
            gameRoot.requestFocus();

            // Set up key event handling on the scene
            gameScene.setOnKeyPressed(event -> {
                if (gameController != null) {
                    String key = event.getCode().toString();

                    // Handle special cases for arrow keys
                    switch (event.getCode()) {
                        case UP -> key = "UP";
                        case DOWN -> key = "DOWN";
                        case LEFT -> key = "LEFT";
                        case RIGHT -> key = "RIGHT";
                        case SPACE -> key = "SPACE";
                        default -> key = event.getText().toUpperCase();
                    }

                    gameController.handleKeyPressed(key);
                }
            });

            System.out.println("Game screen loaded successfully");

        } catch (IOException e) {
            System.err.println("Error loading game screen: " + e.getMessage());
            e.printStackTrace();

            // Fallback: create a simple game screen programmatically
            showFallbackGameScreen();
        }
    }

    private void showFallbackGameScreen() {
        System.out.println("Loading fallback game screen...");

        // Create GameController programmatically
        gameController = new GameController();
        gameController.initialize();
        gameController.startGame();

        // Create a simple UI
        VBox gameLayout = new VBox(10);
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.setPadding(new Insets(20));

        Label gameTitle = new Label("Robot Battle Game");
        gameTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Robot selection buttons
        Button robot1Btn = new Button("Select R1");
        Button robot2Btn = new Button("Select R2");
        Button robot3Btn = new Button("Select R3");
        Button robot4Btn = new Button("Select R4");

        robot1Btn.setOnAction(e -> gameController.selectRobot(0, 0));
        robot2Btn.setOnAction(e -> gameController.selectRobot(0, 1));
        robot3Btn.setOnAction(e -> gameController.selectRobot(1, 0));
        robot4Btn.setOnAction(e -> gameController.selectRobot(1, 1));

        // Control instructions
        Label instructions = new Label("Use WASD or Arrow Keys to move, SPACE to shoot, R to reload");
        instructions.setStyle("-fx-font-size: 12px;");

        // Back button
        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> start(primaryStage));

        gameLayout.getChildren().addAll(gameTitle, robot1Btn, robot2Btn, robot3Btn, robot4Btn, instructions, backButton);

        Scene gameScene = new Scene(gameLayout, 600, 400);

        // Set up key handling
        gameScene.setOnKeyPressed(event -> {
            String key = event.getCode().toString();

            switch (event.getCode()) {
                case UP -> key = "UP";
                case DOWN -> key = "DOWN";
                case LEFT -> key = "LEFT";
                case RIGHT -> key = "RIGHT";
                case SPACE -> key = "SPACE";
                default -> key = event.getText().toUpperCase();
            }

            gameController.handleKeyPressed(key);
        });

        primaryStage.setScene(gameScene);
        gameLayout.requestFocus(); // Focus for key events
    }

    private void showSettingsScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/SettingsController.fxml"));
            VBox settingsRoot = loader.load();
            Scene settingsScene = new Scene(settingsRoot, 400, 300);
            Stage settingsStage = new Stage();
            settingsStage.setScene(settingsScene);
            settingsStage.setTitle("تنظیمات");
            settingsStage.show();
        } catch (IOException e) {
            System.err.println("Error loading settings screen: " + e.getMessage());
            e.printStackTrace();

            // Fallback settings screen
            showFallbackSettingsScreen();
        }
    }

    private void showFallbackSettingsScreen() {
        VBox settingsLayout = new VBox(15);
        settingsLayout.setAlignment(Pos.CENTER);
        settingsLayout.setPadding(new Insets(20));

        Label settingsTitle = new Label("Settings");
        settingsTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Game mode selection
        Label modeLabel = new Label("Game Mode:");
        Button pvpButton = new Button("Player vs Player");
        Button pvaButton = new Button("Player vs AI");
        Button avaButton = new Button("AI vs AI");

        pvpButton.setOnAction(e -> {
            Settings.setBattleMode(BattleMode.PLAYER_VS_PLAYER);
            System.out.println("Game mode set to: Player vs Player");
        });

        pvaButton.setOnAction(e -> {
            Settings.setBattleMode(BattleMode.PLAYER_VS_AI);
            System.out.println("Game mode set to: Player vs AI");
        });

        avaButton.setOnAction(e -> {
            Settings.setBattleMode(BattleMode.AI_VS_AI);
            System.out.println("Game mode set to: AI vs AI");
        });

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> start(primaryStage));

        settingsLayout.getChildren().addAll(settingsTitle, modeLabel, pvpButton, pvaButton, avaButton, backButton);

        Scene settingsScene = new Scene(settingsLayout, 400, 300);
        Stage settingsStage = new Stage();
        settingsStage.setScene(settingsScene);
        settingsStage.setTitle("Settings");
        settingsStage.show();
    }

    public void show() {
        launch(); // شروع UI
    }

    // Static method to launch the application
    public static void main(String[] args) {
        launch(args);
    }
}
