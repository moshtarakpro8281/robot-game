package game;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class SettingsController {

    @FXML
    private RadioButton pvpRadio, pvAIRadio, aiVsAiRadio;

    private ToggleGroup gameTypeGroup = new ToggleGroup();

    private Stage dialogStage;

    private String selectedGameType = "pvp"; // مقدار پیش‌فرض
    private boolean confirmed = false; // Track if user confirmed their choice

    // متد برای ست کردن پنجره
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    // این متد هنگام بارگذاری FXML به صورت خودکار فراخوانی می‌شود
    @FXML
    private void initialize() {
        // اتصال رادیوباتن‌ها به ToggleGroup
        pvpRadio.setToggleGroup(gameTypeGroup);
        pvAIRadio.setToggleGroup(gameTypeGroup);
        aiVsAiRadio.setToggleGroup(gameTypeGroup);

        // مقدار پیش‌فرض انتخاب شده
        pvpRadio.setSelected(true);
    }

    // متد دکمه تأیید
    @FXML
    private void handleConfirm() {
        RadioButton selectedRadio = (RadioButton) gameTypeGroup.getSelectedToggle();

        if (selectedRadio == pvpRadio) {
            selectedGameType = "pvp";
        } else if (selectedRadio == pvAIRadio) {
            selectedGameType = "pvAI";
        } else if (selectedRadio == aiVsAiRadio) {
            selectedGameType = "aiVsAi";
        }

        confirmed = true;
        System.out.println("نوع بازی انتخاب شده: " + selectedGameType);

        if (dialogStage != null) {
            dialogStage.close(); // بستن پنجره تنظیمات
        }
    }

    // Add cancel button handler
    @FXML
    private void handleCancel() {
        confirmed = false;
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    public String getSelectedGameType() {
        return selectedGameType;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}