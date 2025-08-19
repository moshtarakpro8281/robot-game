package game;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class SettingsController {

    // متغیر ToggleGroup برای رادیو دکمه‌ها
    private ToggleGroup gameModeGroup;

    @FXML
    private RadioButton pvpRadio, pvAIRadio, aiVsAiRadio;

    private Stage dialogStage;

    private String selectedGameType = "pvp"; // نوع بازی پیش‌فرض (برای سازگاری با کدهای دیگر)

    // تنظیم پنجره تنظیمات
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    // مقداردهی اولیه
    @FXML
    private void initialize() {
        // ایجاد ToggleGroup و اتصال رادیو دکمه‌ها
        gameModeGroup = new ToggleGroup();
        pvpRadio.setToggleGroup(gameModeGroup);
        pvAIRadio.setToggleGroup(gameModeGroup);
        aiVsAiRadio.setToggleGroup(gameModeGroup);

        // انتخاب پیش‌فرض رادیو دکمه برای نوع بازی
        pvpRadio.setSelected(true);
        // تنظیم پیش‌فرض در Settings
        Settings.setBattleMode(BattleMode.PLAYER_VS_PLAYER);
        System.out.println("Initialized battle mode: " + Settings.getBattleMode());
    }

    // تایید تنظیمات و بستن پنجره
    @FXML
    private void handleConfirm() {
        // دریافت نوع بازی انتخاب شده از رادیو دکمه‌ها
        RadioButton selectedRadio = (RadioButton) gameModeGroup.getSelectedToggle();
        if (selectedRadio == null) {
            System.out.println("Warning: No game mode selected, defaulting to PLAYER_VS_PLAYER");
            Settings.setBattleMode(BattleMode.PLAYER_VS_PLAYER);
            selectedGameType = "pvp";
        } else if (selectedRadio == pvpRadio) {
            Settings.setBattleMode(BattleMode.PLAYER_VS_PLAYER);
            selectedGameType = "pvp";
        } else if (selectedRadio == pvAIRadio) {
            Settings.setBattleMode(BattleMode.PLAYER_VS_AI);
            selectedGameType = "pvAI";
        } else if (selectedRadio == aiVsAiRadio) {
            Settings.setBattleMode(BattleMode.AI_VS_AI);
            selectedGameType = "aiVsAi";
        }

        // نمایش اطلاعات تنظیمات در کنسول (برای دیباگ)
        System.out.println("Selected game mode: " + Settings.getBattleMode() + " (String: " + selectedGameType + ")");

        // بستن پنجره تنظیمات پس از تایید
        dialogStage.close();
    }

    // گرفتن نوع بازی انتخاب شده (برای سازگاری با کدهای دیگر)
    public String getSelectedGameType() {
        return selectedGameType;
    }
}

