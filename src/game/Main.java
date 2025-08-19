package game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // بارگذاری صفحه منو (FXMLDocument.fxml)
        // اصلاح مسیر فایل FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/FXMLDocument.fxml"));  // مسیر نسبی فایل
        Parent root = loader.load();

        // تنظیم صفحه منو به عنوان Scene اولیه
        primaryStage.setTitle("Robot Game");

        // تنظیم اندازه پنجره
        Scene scene = new Scene(root, 800, 600); // اندازه پنجره 800x600
        primaryStage.setScene(scene);

        primaryStage.show(); // نمایش پنجره
    }

    public static void main(String[] args) {
        launch(args);
    }
}
