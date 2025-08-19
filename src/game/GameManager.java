package game;

import java.util.List;

public class GameManager {
    private Board board;
    private List<Player> players;
    private int currentPlayerIndex = 0; // بازیکن فعال

    // تغییر سازنده برای پذیرش لیستی از بازیکنان
    public GameManager(Board board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public void startGame() {
        System.out.println("بازی شروع شد! نقشه: " + board.getWidth() + "x" + board.getHeight());

        // شروع بازی برای هر بازیکن
        while (true) {
            Player currentPlayer = players.get(currentPlayerIndex);
            System.out.println("نوبت بازیکن " + currentPlayer.getName() + " است.");

            // منطق بازی برای بازیکن فعال
            Robot activeRobot = currentPlayer.getActiveRobot();
            if (activeRobot != null) {
                int newX = activeRobot.getX() + 1; // حرکت ربات به راست
                int newY = activeRobot.getY();
                if (newX < board.getWidth() && board.moveRobot(activeRobot, newX, newY)) {
                    System.out.println("ربات فعال به (" + newX + ", " + newY + ") حرکت کرد!");
                } else {
                    System.out.println("حرکت ممکن نیست! (موانع یا خارج از محدوده)");
                }
            } else {
                System.out.println("ربات فعال پیدا نشد!");
            }

            // تغییر نوبت به بازیکن بعدی
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }
}
