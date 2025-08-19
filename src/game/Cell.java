package game;

public class Cell {
    private int x;
    private int y;
    private Entity entity;

    // Constructor
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters for x and y
    public int getX() { return x; }
    public int getY() { return y; }

    // Getter and Setter for entity
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    // Method to remove entity (set to null)
    public void removeEntity() {
        this.entity = null;
    }

    // Method to check if the cell is empty
    public boolean isEmpty() {
        return entity == null;
    }

    // Method to check if the entity is a specific type (e.g., Robot)
    public boolean hasRobot() {
        return entity instanceof Robot;
    }

    // Method to check if the entity is a specific type (e.g., Obstacle)
    public boolean hasObstacle() {
        return entity instanceof Obstacle;
    }

    // New method to check if the cell is walkable
    public boolean isWalkable() {
        return isEmpty() || !hasObstacle(); // سلول قابل عبور است اگر خالی باشد یا مانع نداشته باشد
    }

    // Override toString for easy printing
    @Override
    public String toString() {
        return "Cell{" +
                "x=" + x +
                ", y=" + y +
                ", entity=" + (entity != null ? entity.getClass().getSimpleName() : "null") +
                '}';
    }
}

