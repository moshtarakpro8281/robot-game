package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Game extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // بارگذاری فایل FXML از همان پکیج game
        Parent root = FXMLLoader.load(getClass().getResource("Game.fxml"));
        Scene scene = new Scene(root);

        // اگر لازم است، اندازه صحنه را از FXML بگیر یا تنظیم کن
        // اینجا بدون تعیین اندازه اولیه می‌گذاریم تا از FXML بگیرد

        stage.setTitle("نبرد ربات‌ها");
        stage.setScene(scene);

        // دادن فوکوس به صحنه برای دریافت رویدادهای صفحه‌کلید
        scene.getRoot().requestFocus();

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
