package game;

public class NormalWall extends Obstacle {
    public NormalWall(int x, int y) {
        super(x, y, Obstacle.ObstacleType.NORMAL_WALL);
    }

    @Override
    public void applyEffect(Robot robot) {
        // هیچ آسیبی به ربات نمی‌رسه، فقط از بین می‌ره (این کار تو handleCollision انجام می‌شه)
        System.out.println("ربات به دیوار معمولی در (" + getX() + ", " + getY() + ") برخورد کرد و دیوار از بین رفت!");
    }
}