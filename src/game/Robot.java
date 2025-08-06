package game;

import javafx.scene.paint.Color;
import java.awt.Point;

public class Robot extends Entity {
    private static final int DEFAULT_DAMAGE = 20;  // Default damage value

    private String name;
    private int health;
    private boolean isAlive;
    private int team;
    private int id;
    private Player owner;
    private int ammo;
    private Color color;
    private Direction direction;  // جهت ربات
    private int damage;           // مقدار آسیبی که ربات وارد می‌کند
    private int range;            // برد شلیک ربات

    // سازنده کامل
    public Robot(int row, int col, int team, int id, Player owner, String name) {
        super(row, col);
        this.health = 100;
        this.isAlive = true;
        this.team = team;
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.ammo = 10;
        this.color = Color.GRAY;
        this.direction = Direction.UP;
        this.damage = DEFAULT_DAMAGE;  // مقدار پیش‌فرض آسیب
        this.range = 3;   // برد پیش‌فرض
    }

    // سازنده ساده
    public Robot(int row, int col) {
        super(row, col);
        this.health = 100;
        this.isAlive = true;
        this.team = 1;
        this.id = 1;
        this.owner = null;
        this.name = "Robot " + id;
        this.ammo = 10;
        this.color = Color.GRAY;
        this.direction = Direction.UP;
        this.damage = DEFAULT_DAMAGE;
        this.range = 3;
    }

    // سازنده با رنگ و مهمات برای GameController
    public Robot(int row, int col, Color color, int ammo) {
        super(row, col);
        this.color = color;
        this.ammo = ammo;
        this.health = 100;
        this.isAlive = true;
        this.team = 1;
        this.id = 1;
        this.owner = null;
        this.name = "Robot " + id;
        this.direction = Direction.UP;
        this.damage = 1;
        this.range = 3;
    }

    // سازنده جدید که نام، سطر، ستون و رنگ می‌گیرد (اضافه شده)
    public Robot(String name, int row, int col, Color color) {
        super(row, col);
        this.name = name;
        this.color = color;
        this.health = 100;
        this.isAlive = true;
        this.team = 1;
        this.id = 1;
        this.owner = null;
        this.ammo = 10;
        this.direction = Direction.UP;
        this.damage = 1;
        this.range = 3;
    }

    @Override
    public void update() {
        if (!isAlive) return;
        System.out.println("↻ بروزرسانی ربات: " + name);
    }

    public void takeDamage(int damage) {
        if (!isAlive) return;
        health -= damage;
        if (health <= 0) {
            health = 0;
            isAlive = false;
            System.out.println("💥 " + name + " از تیم " + team + " نابود شد!");
        }
    }

    public void shoot() {
        if (!isAlive) {
            System.out.println("❌ " + name + " از کار افتاده و نمی‌تواند شلیک کند.");
            return;
        }
        if (ammo > 0) {
            ammo--;
            System.out.println("🔫 " + name + " شلیک کرد! مهمات باقی‌مانده: " + ammo);
        } else {
            System.out.println("❌ " + name + " مهمات ندارد.");
        }
    }

    public void destroy() {
        if (!isAlive) return;
        isAlive = false;
        health = 0;
        System.out.println("💥 ربات " + name + " نابود شد و حذف گردید.");
    }

    // Getter و Setter

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public boolean isAlive() { return isAlive; }
    public void setAlive(boolean alive) { isAlive = alive; }

    public int getTeam() { return team; }
    public void setTeam(int team) { this.team = team; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Player getOwner() { return owner; }
    public void setOwner(Player owner) { this.owner = owner; }

    public int getAmmo() { return ammo; }
    public void setAmmo(int ammo) { this.ammo = ammo; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public void decreaseAmmo() {
        if (ammo > 0) ammo--;
    }

    public boolean isDestroyed() {
        return !isAlive;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * Resets the robot's damage to its default value
     */
    public void resetDamage() {
        this.damage = DEFAULT_DAMAGE;  // Reset to default damage value (matching GameController initialization)
        System.out.println("🔄 " + name + " آسیب به مقدار پیش‌فرض بازنشانی شد: " + damage);
    }

    /**
     * Gets the current damage value
     * @return current damage value
     */
    public int getCurrentDamage() {
        return damage;
    }

    /**
     * Reduces the robot's damage by 5% (used after each shot)
     */
    public void reduceDamageBy5Percent() {
        this.damage = (int) Math.max(1, this.damage * 0.95);  // Minimum damage is 1
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    // متدهای مختصات (Row و Col)

    public int getRow() {
        return super.getRow();
    }

    public int getCol() {
        return super.getCol();
    }

    public void setPosition(int row, int col) {
        setRow(row);
        setCol(col);
    }

    public void setPosition(int row, int col, Direction direction) {
        setPosition(row, col);
        this.direction = direction;
    }

    // متدهای get و set جهت

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    // متد اضافه شده برای AIPlayer

    public Point getPositionPoint() {
        return new Point(getCol(), getRow());
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}