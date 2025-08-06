package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        System.out.println("start method is running");

        try {
            // Load the main menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/FXMLDocument.fxml"));
            System.out.println("FXML URL: " + loader.getLocation());
            Parent root = loader.load();

            // Get the main menu controller if you need to pass this Main instance to it
            // FXMLDocumentController controller = loader.getController();
            // controller.setMainApp(this); // If your controller needs reference to this class

            Scene scene = new Scene(root, 450, 450);

            primaryStage.setTitle("Main Menu");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            System.out.println("Stage shown");
        } catch (Exception e) {
            System.out.println("Error loading FXML:");
            e.printStackTrace();
        }
    }

    // Method to be called from your main menu when user clicks "New Game" or similar
    public void openSettingsAndStartGame() {
        try {
            // Load the settings dialog
            FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/game/Settings.fxml"));
            Parent settingsRoot = settingsLoader.load();

            // Get the settings controller
            SettingsController settingsController = settingsLoader.getController();

            // Create and show the settings dialog
            Stage settingsStage = new Stage();
            settingsStage.setTitle("Game Settings");
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(primaryStage);
            settingsStage.setScene(new Scene(settingsRoot));
            settingsStage.setResizable(false);

            // Set the dialog stage in the controller
            settingsController.setDialogStage(settingsStage);

            // Show and wait for user input
            settingsStage.showAndWait();

            // Check if user confirmed their selection
            if (settingsController.isConfirmed()) {
                // Get the selected game type
                String selectedGameType = settingsController.getSelectedGameType();
                System.out.println("Selected game type: " + selectedGameType);

                // Start the game with the selected settings
                startGameWithSettings(selectedGameType);
            } else {
                System.out.println("Settings canceled by user");
                // User canceled, return to main menu
            }
        } catch (IOException e) {
            System.out.println("Error loading settings:");
            e.printStackTrace();
        }
    }

    private void startGameWithSettings(String gameMode) {
        try {
            // Load the game FXML
            FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("/game/Game.fxml"));
            Parent gameRoot = gameLoader.load();

            // Get the game controller
            GameController gameController = gameLoader.getController();

            // Set the game mode BEFORE starting the game
            gameController.setGameMode(gameMode);

            // Start the game (this will initialize players with the correct mode)
            gameController.startGame();

            // Create and show the game stage
            Stage gameStage = new Stage();
            String modeText = switch (gameMode) {
                case "pvp" -> "Player vs Player";
                case "pvAI" -> "Player vs AI";
                case "aiVsAi" -> "AI vs AI";
                default -> "Unknown Mode";
            };
            gameStage.setTitle("Robot Battle Game - " + modeText);
            gameStage.setScene(new Scene(gameRoot));
            gameStage.setResizable(false);
            gameStage.show();

            // Hide the main menu while game is running
            primaryStage.hide();

            // Set what happens when game window is closed
            gameStage.setOnCloseRequest(e -> {
                // Show main menu again when game ends
                primaryStage.show();
            });

        } catch (IOException e) {
            System.out.println("Error loading game:");
            e.printStackTrace();
        }
    }

    // Method to be called from main menu for other buttons
    public void showAbout() {
        System.out.println("About clicked");
        // Implement about dialog if needed
    }

    public void exitApplication() {
        System.out.println("Exiting application");
        primaryStage.close();
    }

    // Getter for primary stage if needed by controllers
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        System.out.println("Launching application...");
        launch(args);
    }
}
