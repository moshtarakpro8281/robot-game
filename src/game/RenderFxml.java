package game;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;

public class RenderFxml {/*

    @FXML
    private RadioButton pvpRadio, pvAIRadio, aiVsAiRadio;

    @FXML
    private Spinner<Integer> widthSpinner, heightSpinner;

    private ToggleGroup gameTypeGroup = new ToggleGroup();

    private GameController controller; // Reference to the existing controller

    @FXML
    private void initialize() {
        pvpRadio.setToggleGroup(gameTypeGroup);
        pvAIRadio.setToggleGroup(gameTypeGroup);
        aiVsAiRadio.setToggleGroup(gameTypeGroup);

        // Set default selection
        pvpRadio.setSelected(true);

        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 15));
        heightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 15));
    }

    // Method to set the existing controller reference
    public void setController(GameController controller) {
        this.controller = controller;
    }

    @FXML
    private void handleConfirm() {
        // Get selected game type
        RadioButton selectedRadio = (RadioButton) gameTypeGroup.getSelectedToggle();
        if (selectedRadio == null) {
            System.out.println("هیچ نوع بازی انتخاب نشده است!");
            return;
        }

        String gameType = selectedRadio.getText();

        // Get map dimensions from spinners
        int width = widthSpinner.getValue();
        int height = heightSpinner.getValue();

        System.out.println("نوع بازی: " + gameType);
        System.out.println("ابعاد نقشه: " + width + " x " + height);

        // Determine battle mode based on selection
        BattleMode battleMode;
        if (selectedRadio == pvpRadio) {
            battleMode = BattleMode.PLAYER_VS_PLAYER;
        } else if (selectedRadio == pvAIRadio) {
            battleMode = BattleMode.PLAYER_VS_AI;
        } else if (selectedRadio == aiVsAiRadio) {
            battleMode = BattleMode.AI_VS_AI;
        } else {
            battleMode = BattleMode.PLAYER_VS_PLAYER; // Default
        }

        // Set the battle mode in Settings (assuming Settings class exists)
        try {
            Settings.setBattleMode(battleMode);
        } catch (Exception e) {
            System.out.println("خطا در تنظیم حالت بازی: " + e.getMessage());
        }

        // If controller is not set, you might need to get it from somewhere else
        if (controller == null) {
            System.out.println("خطا: کنترلر تنظیم نشده است!");
            return;
        }

        try {
            // Note: The current GameController is designed for 15x15 maps
            // If you want to support dynamic sizing, you'll need to modify GameController
            // For now, we'll just set the game mode

            controller.setGameMode(battleMode);

            // If you want to support dynamic map sizes, you would need to:
            // 1. Modify GameController to accept dynamic map dimensions
            // 2. Update the initialization logic
            // 3. Handle robot placement for different map sizes

            // For now, just reinitialize with the selected game mode
            controller.initialize();

            // Create and set render fixer if needed
            if (controller.getRenderFixer() == null) {
                RenderFixer renderFixer = new RenderFixer();
                controller.setRenderFixer(renderFixer);
            }

            // Render the map
            controller.renderMap();

            System.out.println("تنظیمات اعمال شد!");

        } catch (Exception e) {
            System.out.println("خطا در اعمال تنظیمات: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to get selected battle mode (utility method)
    public BattleMode getSelectedBattleMode() {
        RadioButton selectedRadio = (RadioButton) gameTypeGroup.getSelectedToggle();
        if (selectedRadio == pvpRadio) {
            return BattleMode.PLAYER_VS_PLAYER;
        } else if (selectedRadio == pvAIRadio) {
            return BattleMode.PLAYER_VS_AI;
        } else if (selectedRadio == aiVsAiRadio) {
            return BattleMode.AI_VS_AI;
        }
        return BattleMode.PLAYER_VS_PLAYER; // Default
    }

    // Method to get selected dimensions (utility method)
    public int getSelectedWidth() {
        return widthSpinner.getValue();
    }

    public int getSelectedHeight() {
        return heightSpinner.getValue();
    }*/
}


