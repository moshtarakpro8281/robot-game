package game;

public class WoodenWall extends Obstacle {
    private int hitPoints = 1;

    public WoodenWall(int row, int col) {
        super(row, col, ObstacleType.WOODEN_WALL);
    }

    public int getHitPoints() {
        return hitPoints;
    }

    @Override
    public void applyEffect(Robot robot) {
        if (robot.isAlive()) {
            robot.takeDamage(10);
            System.out.println("ربات به دیوار چوبی در (" + getCol() + ", " + getRow() + ") برخورد کرد و 10% سلامتش کم شد!");
        }
    }

    public boolean takeBullet(Board board) {
        hitPoints--;
        System.out.println("دیوار چوبی در (" + getCol() + ", " + getRow() + ") یک گلوله خورد! باقی‌مانده: " + hitPoints);
        if (hitPoints <= 0) {
            System.out.println("دیوار چوبی در (" + getCol() + ", " + getRow() + ") نابود شد!");
            board.getCell(getCol(), getRow()).removeEntity();
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        // نیازی به بروزرسانی نیست ولی متد abstract رو override کردیم
    }
}
