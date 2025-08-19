package game;

import java.util.List;

public class Player {
    private String name;
    private boolean isAI;
    private List<Robot> robots;
    private int activeRobotIndex = 0; // ربات فعال

    // سازنده که نام، نوع بازیکن (انسان یا AI) و لیست ربات‌ها را می‌گیرد
    public Player(String name, boolean isAI, List<Robot> robots) {
        this.name = name;
        this.isAI = isAI;
        this.robots = robots;
        if (!robots.isEmpty()) {
            activeRobotIndex = 0; // ربات اول به‌صورت پیش‌فرض فعاله
        }
    }

    // متد برای اضافه کردن ربات به لیست ربات‌ها
    public void addRobot(Robot robot) {
        robots.add(robot); // ربات را به لیست ربات‌ها اضافه می‌کند
    }

    // متد برای گرفتن لیست ربات‌ها
    public List<Robot> getRobots() {
        return robots;
    }

    // متد برای گرفتن ربات فعال
    public Robot getActiveRobot() {
        if (activeRobotIndex >= 0 && activeRobotIndex < robots.size()) {
            return robots.get(activeRobotIndex);
        }
        return null; // اگر لیست خالی باشد یا ایندکس نامعتبر باشد
    }

    // متد برای تنظیم ربات فعال
    public void setActiveRobot(int index) {
        if (index >= 0 && index < robots.size()) {
            activeRobotIndex = index;
            System.out.println("ربات شماره " + (index + 1) + " فعال شد!");
        } else {
            System.out.println("ایندکس نامعتبر است!");
        }
    }

    // متد برای گرفتن اندیس ربات فعال
    public int getActiveRobotIndex() {
        return activeRobotIndex;
    }

    // متد برای حذف ربات از لیست
    public void removeRobot(Robot robot) {
        robots.remove(robot);
        // اگر ربات حذف شده باشد و ربات فعال قبلی حذف شود، ربات جدیدی به طور خودکار فعال می‌شود
        if (activeRobotIndex >= robots.size()) {
            activeRobotIndex = robots.size() - 1; // اگر ربات حذف شده بود، ربات جدیدی فعال می‌شود
        }
        System.out.println("ربات " + robot.getName() + " حذف شد!");
    }

    // متد برای گرفتن نام بازیکن
    public String getName() {
        return name;
    }

    // متد برای چک کردن اینکه بازیکن AI است یا نه
    public boolean isAI() {
        return isAI;
    }
}


