package game;

public class SteelWall extends Obstacle {
    private int hitCount = 0;

    public SteelWall(int x, int y) {
        super(x, y, ObstacleType.STEEL_WALL);
    }

    @Override
    public void applyEffect(Robot robot) {
        // دیوار فولادی هیچ آسیبی به ربات وارد نمی‌کند
        if (isDestroyed()) {
            System.out.println("دیوار فولادی در (" + getX() + ", " + getY() + ") نابود شده است.");
        }
    }

    public void hit() {
        if (!isDestroyed()) {
            hitCount++;
            System.out.println("دیوار فولادی در (" + getX() + ", " + getY() + ") شلیک خورد. تعداد شلیک‌ها: " + hitCount);

            if (hitCount >= 3) {
                System.out.println("دیوار فولادی در (" + getX() + ", " + getY() + ") با 3 گلوله نابود شد!");
            }
        }
    }

    public int getHitCount() {
        return hitCount;
    }

    public boolean isDestroyed() {
        return hitCount >= 3; // نابود می‌شود بعد از 3 گلوله
    }
}
