package game;

public abstract class Mine extends Obstacle {
    public Mine(int x, int y, ObstacleType type) {
        super(x, y, type);
    }

    public abstract void applyEffect(Robot robot);
}