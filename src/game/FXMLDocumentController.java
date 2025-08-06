package game;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;

    private String selectedGameType = "pvp";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // مقداردهی اولیه
        if (label != null) {
            label.setText("نوع بازی: " + selectedGameType);
        }
    }

    @FXML
    private void handleStartGame(ActionEvent event) {
        // First open settings, then start game with selected mode
        openSettingsAndStartGame();
    }

    private void openSettingsAndStartGame() {
        try {
            // Load the settings dialog first
            System.out.println("Loading Settings FXML: " + getClass().getResource("/game/Settings.fxml"));
            FXMLLoader settingsLoader = new FXMLLoader(getClass().getResource("/game/Settings.fxml"));
            Parent settingsRoot = settingsLoader.load();

            // Get the settings controller
            SettingsController settingsController = settingsLoader.getController();

            // Create and show the settings dialog
            Stage settingsStage = new Stage();
            settingsStage.setTitle("تنظیمات بازی");
            settingsStage.initModality(Modality.APPLICATION_MODAL);
            settingsStage.setScene(new Scene(settingsRoot));
            settingsStage.setResizable(false);

            // Set the dialog stage in the controller
            settingsController.setDialogStage(settingsStage);

            // Show and wait for user input
            settingsStage.showAndWait();

            // Check if user confirmed their selection
            if (settingsController.isConfirmed()) {
                // Get the selected game type
                selectedGameType = settingsController.getSelectedGameType();
                System.out.println("Selected game type: " + selectedGameType);

                // Update the label
                if (label != null) {
                    label.setText("نوع بازی انتخاب شده: " + selectedGameType);
                }

                // Start the game with the selected settings
                startGameWithSettings(selectedGameType);
            } else {
                System.out.println("Settings canceled by user");
                // User canceled, stay on main menu
            }
        } catch (IOException e) {
            System.out.println("Error loading settings:");
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "خطا در لود تنظیمات: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void startGameWithSettings(String gameMode) {
        try {
            System.out.println("Loading Game FXML: " + getClass().getResource("/game/Game.fxml"));
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
                case "pvp" -> "بازیکن در برابر بازیکن";
                case "pvAI" -> "بازیکن در برابر هوش مصنوعی";
                case "aiVsAi" -> "هوش مصنوعی در برابر هوش مصنوعی";
                default -> "حالت نامشخص";
            };
            gameStage.setTitle("نبرد ربات‌ها - " + modeText);
            gameStage.setScene(new Scene(gameRoot));
            gameStage.setResizable(false);
            gameStage.show();

            System.out.println("Game started with mode: " + gameMode);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "خطا در لود صفحه بازی: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            System.out.println("Loading Settings FXML: " + getClass().getResource("/game/Settings.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/Settings.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("تنظیمات بازی");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            SettingsController controller = loader.getController();
            controller.setDialogStage(stage);

            stage.showAndWait();

            // Only update if user confirmed
            if (controller.isConfirmed()) {
                selectedGameType = controller.getSelectedGameType();

                if (label != null) {
                    label.setText("نوع بازی انتخاب شده: " + selectedGameType);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "خطا در لود تنظیمات: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        System.exit(0);
    }
}