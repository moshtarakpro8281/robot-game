package game;

import java.util.Random;

public class Board {
    private Cell[][] cells;
    private int width;
    private int height;
    private Random random = new Random();

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell(x, y);
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public Cell getCell(int x, int y) { return isInBounds(x, y) ? cells[y][x] : null; }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void addObstacles(GameConfig config) {
        for (int i = 0; i < config.getMineCount(); i++) {
            int x, y;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (getCell(x, y).getEntity() != null);
            getCell(x, y).setEntity(new Mine(x, y, Obstacle.ObstacleType.MINE));
        }
    }

    public void generateTestMap() {
        // مین‌ها رو به‌صورت دستی اضافه می‌کنم
        getCell(2, 2).setEntity(new Mine(2, 2, Obstacle.ObstacleType.MINE));
        getCell(4, 4).setEntity(new Mine(4, 4, Obstacle.ObstacleType.MINE));
        getCell(6, 6).setEntity(new Mine(6, 6, Obstacle.ObstacleType.MINE));
    }

    public boolean moveRobot(Robot robot, int newRow, int newCol) {
        if (!isInBounds(newCol, newRow)) return false; // توجه: x=col, y=row
        Cell currentCell = getCell(robot.getCol(), robot.getRow());
        Cell newCell = getCell(newCol, newRow);
        if (newCell.getEntity() == null) {
            currentCell.removeEntity();
            newCell.setEntity(robot);
            robot.setPosition(newRow, newCol);
            return true;
        }
        return false;
    }

    public boolean shoot(Robot shooter, int targetX, int targetY) {
        if (!isInBounds(targetX, targetY)) return false;
        Entity target = getCell(targetX, targetY).getEntity();
        if (target != null && target != shooter) {
            if (target instanceof SteelWall) {
                SteelWall wall = (SteelWall) target;
                wall.takeBullet(this);
            } else if (target instanceof WoodenWall) {
                WoodenWall wall = (WoodenWall) target;
                wall.takeBullet(this);
            } else if (target instanceof Obstacle) {
                ((Obstacle) target).applyEffect(shooter); // اعمال اثر موانع دیگر
            } else if (target instanceof Robot) {
                ((Robot) target).takeDamage(33); // آسیب به ربات هدف
            }
            return true; // برخورد موفق
        }
        return false;
    }

    public Entity getEntityAt(int x, int y) {
        if (isInBounds(x, y)) {
            return getCell(x, y).getEntity();
        }
        return null;
    }
}
