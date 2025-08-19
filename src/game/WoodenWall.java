package game;

public class WoodenWall extends Obstacle {

    private boolean isDestroyed; // متغیر برای ذخیره وضعیت نابود شدن دیوار

    public WoodenWall(int x, int y) {
        super(x, y, ObstacleType.WOODEN_WALL);
        this.isDestroyed = false; // در ابتدا دیوار نابود نشده است
    }

    @Override
    public void applyEffect(Robot robot) {
        // دیوار چوبی هیچ آسیبی به ربات وارد نمی‌کند.
        if (isDestroyed) {
            System.out.println("دیوار چوبی در (" + getX() + ", " + getY() + ") نابود شده است.");
        }
    }

    public void takeShot() {
        if (!isDestroyed) {
            isDestroyed = true; // دیوار نابود می‌شود
            System.out.println("دیوار چوبی در (" + getX() + ", " + getY() + ") با شلیک گلوله نابود شد!");
        }
    }
}
