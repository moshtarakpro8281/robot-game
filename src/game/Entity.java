package game;

public abstract class Entity {
    private int row;  // ردیف در صفحه
    private int col;  // ستون در صفحه
    private boolean visible = true;

    public Entity(int row, int col) {
        this.row = row;
        this.col = col;
        this.visible = true;
    }

    protected Entity() {
        this(0, 0);
    }

    // متدهای getter و setter
    public int getRow() { return row; }
    public int getCol() { return col; }

    public void setRow(int row) { this.row = row; }
    public void setCol(int col) { this.col = col; }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    /**
     * فاصله منهتن بین دو موجودیت
     */
    public int distanceTo(Entity other) {
        return Math.abs(this.row - other.row) + Math.abs(this.col - other.col);
    }

    /**
     * بررسی اینکه آیا دو موجودیت در یک موقعیت هستند یا خیر
     */
    public boolean isAtSamePosition(Entity other) {
        return this.row == other.row && this.col == other.col;
    }

    /**
     * متد انتزاعی که هر کلاس فرزند باید پیاده‌سازی کند
     */
    public abstract void update();
}
