package game;

public class Cell {
    private int x;
    private int y;
    private Entity entity;

    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void removeEntity() {
        this.entity = null;
    }

    // تعیین مانع (Obstacle)
    public void setObstacle(Obstacle obstacle) {
        this.entity = obstacle;
    }

    // بررسی اینکه آیا سلول خالی است
    public boolean isEmpty() {
        return entity == null;
    }

    // بررسی اینکه آیا مانع در سلول هست
    public boolean hasObstacle() {
        return entity instanceof Obstacle;
    }

    // بررسی اینکه آیا ربات در سلول هست
    public boolean hasRobot() {
        return entity instanceof Robot;
    }

    // گرفتن ربات
    public Robot getRobot() {
        if (hasRobot()) {
            return (Robot) entity;
        }
        return null;
    }

    // گرفتن مانع
    public Obstacle getObstacle() {
        if (hasObstacle()) {
            return (Obstacle) entity;
        }
        return null;
    }
}
