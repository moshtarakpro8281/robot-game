package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleRobots {
    private Board board;
    private List<Robot> robots;
    private List<Player> players; // لیست بازیکنان
    private GameManager gameManager;
    private GameUI gameUI;
    private GameConfig config;
    private Random random;

    public BattleRobots() {
        // 1. تنظیم اولیه با خواندن GameConfig
        config = new GameConfig(); // خواندن تنظیمات مثل اندازه نقشه و تعداد ربات‌ها
        board = new Board(config.getMapWidth(), config.getMapHeight()); // ساخت نقشه از تنظیمات
        board.addObstacles(config); // اضافه کردن موانع مثل مین‌ها
        board.generateTestMap(); // تنظیم نقشه تست

        // 2. ایجاد و قرار دادن ربات‌ها
        robots = new ArrayList<>();
        random = new Random();
        initializeRobots(); // ربات‌ها رو به‌صورت تصادفی روی نقشه قرار می‌ده

        // 3. ایجاد بازیکنان و نسبت دادن ربات‌ها به آنها
        players = new ArrayList<>();
        Player p1 = new Player("Player1", false, robots.subList(0, 2));  // بازیکن اول (انسان)
        Player p2 = new Player("Player2", true, robots.subList(2, 4));   // بازیکن دوم (AI)
        players.add(p1);
        players.add(p2);

        // فرض می‌کنیم هر بازیکن 2 ربات دارد
        p1.setActiveRobot(0); // تنظیم ربات فعال برای بازیکن اول
        p2.setActiveRobot(0); // تنظیم ربات فعال برای بازیکن دوم

        // 4. تنظیم GameManager برای مدیریت بازی
        gameManager = new GameManager(board, players); // ارسال لیست بازیکنان به GameManager

        // 5. فعال کردن UI اگر تنظیمات اجازه بدهد
        if (config.isUIEnabled()) {
            gameUI = new GameUI(board, p1, p2, gameManager); // UI برای نمایش گرافیکی
            gameUI.show(); // نمایش رابط کاربری
        }

        // 6. شروع بازی
        gameManager.startGame(); // اجرای بازی از طریق GameManager
    }

    private void initializeRobots() {
        // قرار دادن ربات‌ها به‌صورت تصادفی روی نقشه 10x10
        int robotCount = config.getRobotCount(); // تعداد ربات‌ها از config
        for (int i = 0; i < robotCount; i++) {
            int x, y;
            do {
                x = random.nextInt(10); // مختصات تصادفی بین 0 تا 9
                y = random.nextInt(10);
            } while (board.getCell(x, y).getEntity() != null); // چک کن که سلول خالی باشه

            // تخصیص تیم و جهت به ربات‌ها به‌صورت تصادفی
            String team = (i % 2 == 0) ? "Red" : "Blue"; // ربات‌ها به‌طور تصادفی به تیم‌ها تخصیص داده می‌شوند
            Direction direction = (i % 2 == 0) ? Direction.RIGHT : Direction.LEFT; // جهت ربات‌ها

            // اضافه کردن نام ربات‌ها
            String name = "Robot" + (i + 1); // نام ربات‌ها به صورت "Robot1", "Robot2" و ...

            // ساخت ربات با نام جدید
            Robot robot = new Robot(name, x, y, team, direction); // اصلاح این خط برای استفاده از 5 پارامتر
            board.getCell(x, y).setEntity(robot);
            robots.add(robot);
        }
    }

    public static void main(String[] args) {
        new BattleRobots(); // نقطه شروع اجرای کل پروژه
    }
}

