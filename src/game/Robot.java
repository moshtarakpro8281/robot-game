package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Robot extends Entity {
    private int health = 100;
    private boolean isAlive = true;
    private int x;  // موقعیت x
    private int y;  // موقعیت y
    private String team;  // فیلد برای ذخیره تیم ربات
    private Direction direction;  // فیلد برای ذخیره جهت حرکت ربات
    private String name;  // فیلد برای ذخیره نام ربات
    private int ammo;  // فیلد برای ذخیره مهمات ربات
    private int range;  // فیلد برای ذخیره فاصله شلیک ربات
    private int damage;  // فیلد برای ذخیره آسیب ربات
    private int barrelLength;  // فیلد برای ذخیره طول لوله شلیک ربات
    private Color color;  // فیلد برای ذخیره رنگ ربات
    private boolean hasShot = false;  // آیا ربات شلیک کرده است؟

    // سازنده جدید که نام، تیم و جهت را هم می‌گیرد
    public Robot(String name, int x, int y, String team, Direction direction) {
        super(x, y);  // استفاده از سازنده کلاس پدر (Entity)
        this.name = name;  // تنظیم نام ربات
        this.team = team;
        this.direction = direction;
        this.x = x;
        this.y = y;
        this.ammo = 10; // هماهنگ با GameController
        this.range = 3; // هماهنگ با GameController
        this.damage = 20; // هماهنگ با GameController
        this.barrelLength = 20; // هماهنگ با GameController
        this.color = team.equals("Red") ? Color.RED : Color.BLUE; // تنظیم رنگ بر اساس تیم
    }

    // متدهای getter و setter برای نام ربات
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // متدهای getter و setter برای تیم
    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
        this.color = team.equals("Red") ? Color.RED : Color.BLUE; // تغییر رنگ بر اساس تیم
    }

    // متدهای getter و setter برای سلامت و وضعیت زنده بودن
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
        this.isAlive = health > 0; // به‌روزرسانی وضعیت زنده بودن
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        this.isAlive = alive;
    }

    // متد برای دریافت آسیب
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            isAlive = false;
        }
    }

    // متد برای تنظیم موقعیت ربات
    public void setPosition(int x, int y) {
        if (isAlive) {  // اگر ربات زنده است، موقعیت تغییر می‌کند
            this.x = x;
            this.y = y;
        }
    }

    // متدهای getter برای موقعیت
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // متد برای دریافت و تنظیم جهت ربات
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // متد برای دریافت ردیف ربات
    public int getRow() {
        return x;  // استفاده از x برای دریافت ردیف ربات
    }

    // متد برای دریافت ستون ربات
    public int getCol() {
        return y;  // استفاده از y برای دریافت ستون ربات
    }

    // متد برای دریافت مهمات
    public int getAmmo() {
        return ammo;
    }

    // متد برای تنظیم مهمات ربات
    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    // متد برای کاهش مهمات ربات
    public void decreaseAmmo() {
        if (ammo > 0) {
            ammo--;
        }
    }

    // متد برای دریافت فاصله شلیک ربات
    public int getRange() {
        return range;
    }

    // متد برای تنظیم فاصله شلیک ربات
    public void setRange(int range) {
        this.range = range;
    }

    // متد برای دریافت آسیب
    public int getDamage() {
        return damage;
    }

    // متد برای تنظیم آسیب ربات
    public void setDamage(int damage) {
        this.damage = damage;
    }

    // متد برای تنظیم طول لوله شلیک
    public void setBarrelLength(int barrelLength) {
        this.barrelLength = barrelLength;
    }

    // متد برای دریافت طول لوله شلیک
    public int getBarrelLength() {
        return barrelLength;
    }

    // متد برای دریافت رنگ ربات
    public Color getColor() {
        return color;
    }

    // متد برای تنظیم مجدد آسیب ربات
    public void resetDamage() {
        this.damage = 20; // مقدار پیش‌فرض دمیج
    }

    // متد برای دریافت آسیب فعلی
    public int getCurrentDamage() {
        return getDamage();  // همان آسیب فعلی را بر می‌گرداند
    }

    // متد برای کاهش آسیب ربات به اندازه 5٪
    public void reduceDamageBy5Percent() {
        this.damage = (int) (this.damage * 0.95);  // کاهش 5٪ از آسیب
    }

    // متد برای نمایش ربات در رابط گرافیکی
    @Override
    public Circle getView() {
        return new Circle(15, 15, 12, color);
    }

    // متدهای حرکت ربات
    public void moveRight() {
        if (isAlive && !hasShot) {
            x++;  // افزایش موقعیت x به سمت راست
        }
    }

    public void moveLeft() {
        if (isAlive && !hasShot) {
            x--;  // کاهش موقعیت x به سمت چپ
        }
    }

    public void moveUp() {
        if (isAlive && !hasShot) {
            y--;  // کاهش موقعیت y به سمت بالا
        }
    }

    public void moveDown() {
        if (isAlive && !hasShot) {
            y++;  // افزایش موقعیت y به سمت پایین
        }
    }

    // متد برای شلیک
    public void shoot() {
        if (isAlive) {
            hasShot = true;  // وقتی شلیک شد، این متغیر به true تغییر می‌کند
            System.out.println(name + " شلیک کرد!");
        }
    }

    // متد برای بررسی اینکه ربات شلیک کرده است
    public boolean hasShot() {
        return hasShot;
    }

    // متد برای ریست کردن وضعیت شلیک
    public void resetShot() {
        this.hasShot = false;
    }
}
