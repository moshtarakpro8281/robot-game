package game;

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
    private Label label; // برای نمایش پیام

    private String selectedGameType = "pvp"; // نوع بازی پیش‌فرض

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // مقداردهی اولیه (اگر لازم است)
        label.setText("نوع بازی انتخاب شده: " + selectedGameType);
    }

    // متد شروع بازی
    @FXML
    private void handleStartGame(ActionEvent event) {
        try {
            // بارگذاری صفحه بازی
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/Game.fxml"));
            Parent root = loader.load();

            // گرفتن Stage فعلی
            Stage stage = (Stage) label.getScene().getWindow();

            // تغییر Scene به صفحه بازی
            Scene gameScene = new Scene(root);
            stage.setScene(gameScene);

            // بعد از تغییر Scene، پیام "شروع بازی" نمایش داده می‌شود
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("شروع بازی");
            alert.setHeaderText(null);
            alert.setContentText("شروع بازی انجام شد! نوع بازی: " + selectedGameType);
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("خطا در بارگذاری صفحه بازی");
        }
    }

    // متد تنظیمات بازی
    @FXML
    private void handleSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/Settings.fxml"));
            Parent root = loader.load();

            // پنجره تنظیمات
            Stage stage = new Stage();
            stage.setTitle("تنظیمات بازی");
            stage.initModality(Modality.APPLICATION_MODAL); // جلوگیری از کار با پنجره اصلی
            stage.setScene(new Scene(root));

            // گرفتن کنترلر پنجره تنظیمات
            SettingsController controller = loader.getController();
            controller.setDialogStage(stage);

            stage.showAndWait(); // منتظر بمان تا پنجره بسته شود

            // دریافت نوع بازی انتخاب شده بعد از بسته شدن پنجره تنظیمات
            selectedGameType = controller.getSelectedGameType();

            // به‌روزرسانی نمایش نوع بازی
            if (label != null) {
                label.setText("نوع بازی انتخاب شده: " + selectedGameType);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("خطا در بارگذاری تنظیمات");
        }
    }

    // متد برای بستن پنجره
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close(); // بستن پنجره
    }

    // متد نمایش پیام خطا
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("خطا");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // گرفتن نوع بازی انتخاب شده
    public String getSelectedGameType() {
        return selectedGameType;
    }

    // متد برای حرکت ربات به سمت چپ
    @FXML
    public void moveRobotLeft(ActionEvent event) {
        // کدهایی که حرکت ربات به سمت چپ را انجام می‌دهند
        System.out.println("Robot moved left!");
        // اینجا می‌توانید کد حرکت ربات را قرار دهید
    }
}
