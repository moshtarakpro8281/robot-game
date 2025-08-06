package game;

public class Mine extends Obstacle {

    // سازنده ساده با مقدار ثابت برای نوع مین
    public Mine(int row, int col) {
        super(row, col, ObstacleType.MINE);
    }

    // اگر نیاز داری سازنده با پارامتر نوع هم نگه داری:
    public Mine(int row, int col, ObstacleType type) {
        super(row, col, type);
    }

    @Override
    public void applyEffect(Robot robot) {
        if (robot.isAlive()) {
            robot.takeDamage(33); // کاهش 33% سلامت
            System.out.println("ربات در (" + getCol() + ", " + getRow() + ") با مین برخورد کرد و 33% سلامتش کم شد!");
        }
    }

    // پیاده سازی متد abstract update
    @Override
    public void update() {
        // مین معمولاً نیازی به بروزرسانی ندارد اما این متد باید وجود داشته باشد
    }
}

