package game;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundManager {
    private static MediaPlayer moveSound;
    private static MediaPlayer shootSound;

    static {
        // مسیر فایل‌های صوتی (از منابع پروژه)
        URL movePath = SoundManager.class.getResource("/sound/move.mp3");
        URL shootPath = SoundManager.class.getResource("/sound/shoot.mp3");
        if (movePath != null && shootPath != null) {
            moveSound = new MediaPlayer(new Media(movePath.toString()));
            shootSound = new MediaPlayer(new Media(shootPath.toString()));
        } else {
            System.err.println("فایل‌های صوتی move.mp3 یا shoot.mp3 یافت نشد!");
        }
    }

    public static void playMoveSound() {
        if (moveSound != null) {
            moveSound.stop();
            moveSound.play();
        }
    }

    public static void playShootSound() {
        if (shootSound != null) {
            shootSound.stop();
            shootSound.play();
        }
    }
}