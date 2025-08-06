package game;

import javafx.scene.paint.Color;

public class SteelWall extends Obstacle {
    private int hitPoints = 3;

    public SteelWall(int row, int col) {
        super(row, col, ObstacleType.STEEL_WALL);
    }

    public int getHitPoints() {
        return hitPoints;
    }

    @Override
    public void applyEffect(Robot robot) {
        // فقط مانع حرکت می‌شود
        System.out.println("ربات به دیوار فولادی در (" + getCol() + ", " + getRow() + ") برخورد کرد!");
    }

    /**
     * کاهش جان دیوار هنگام شلیک گلوله.
     * @param board مرجع به صفحه بازی برای حذف موجودیت در صورت نابودی
     * @return true اگر دیوار نابود شده باشد، false در غیر اینصورت
     */
    public boolean takeBullet(Board board) {
        hitPoints--;
        System.out.println("دیوار فولادی در (" + getCol() + ", " + getRow() + ") یک گلوله خورد! باقی‌مانده: " + hitPoints);
        if (hitPoints <= 0) {
            System.out.println("دیوار فولادی در (" + getCol() + ", " + getRow() + ") نابود شد!");
            board.getCell(getCol(), getRow()).removeEntity();
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        // دیوار فولادی نیاز به بروزرسانی خاصی ندارد، ولی باید این متد override شود.
    }
}


