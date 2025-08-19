package game;

import java.util.Random;

public class Board {
    private Cell[][] grid;
    private int width;
    private int height;
    private Random random = new Random();

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                grid[i][j] = new Cell(j, i);
            }
        }
    }

    public void addObstacles(GameConfig config) {
        // چک کردن هماهنگی ابعاد
        if (config.getMapWidth() != width || config.getMapHeight() != height) {
            System.out.println("Warning: GameConfig dimensions do not match Board dimensions!");
            return;
        }
        // افزودن 2 دیوار چوبی
        for (int i = 0; i < 2; i++) {
            int x, y;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (grid[y][x].getEntity() != null);
            grid[y][x].setEntity(new WoodenWall(x, y));
            System.out.println("Added WoodenWall at (" + x + ", " + y + ")");
        }
        // افزودن 2 دیوار فولادی
        for (int i = 0; i < 2; i++) {
            int x, y;
            do {
                x = random.nextInt(width);
                y = random.nextInt(height);
            } while (grid[y][x].getEntity() != null);
            grid[y][x].setEntity(new SteelWall(x, y));
            System.out.println("Added SteelWall at (" + x + ", " + y + ")");
        }
        // افزودن 1 دیوار معمولی
        int x, y;
        do {
            x = random.nextInt(width);
            y = random.nextInt(height);
        } while (grid[y][x].getEntity() != null);
        grid[y][x].setEntity(new NormalWall(x, y));
        System.out.println("Added NormalWall at (" + x + ", " + y + ")");
        // افزودن مین‌های مخفی
        for (int i = 0; i < config.getMineCount(); i++) {
            int x1, y1;
            do {
                x1 = random.nextInt(width);
                y1 = random.nextInt(height);
            } while (grid[y1][x1].getEntity() != null);
            grid[y1][x1].setEntity(new HiddenMine(x1, y1));
            System.out.println("Added HiddenMine at (" + x1 + ", " + y1 + ")");
        }
        // افزودن مین‌های آشکار
        for (int i = 0; i < config.getMineCount() / 2; i++) {
            int x1, y1;
            do {
                x1 = random.nextInt(width);
                y1 = random.nextInt(height);
            } while (grid[y1][x1].getEntity() != null);
            grid[y1][x1].setEntity(new VisibleMine(x1, y1));
            System.out.println("Added VisibleMine at (" + x1 + ", " + y1 + ")");
        }
    }

    public void generateTestMap() {
        // چک کردن ابعاد نقشه
        if (width < 8 || height < 8) {
            System.out.println("Warning: Test map requires at least 8x8 grid");
            return;
        }
        // اضافه کردن موانع با چک کردن خالی بودن
        if (grid[1][1].getEntity() == null) grid[1][1].setEntity(new SteelWall(1, 1));
        if (grid[2][2].getEntity() == null) grid[2][2].setEntity(new WoodenWall(2, 2));
        if (grid[3][3].getEntity() == null) grid[3][3].setEntity(new SteelWall(3, 3));
        if (grid[4][4].getEntity() == null) grid[4][4].setEntity(new WoodenWall(4, 4));
        if (grid[5][5].getEntity() == null) grid[5][5].setEntity(new NormalWall(5, 5));
        if (grid[6][6].getEntity() == null) grid[6][6].setEntity(new HiddenMine(6, 6));
        if (grid[7][7].getEntity() == null) grid[7][7].setEntity(new VisibleMine(7, 7));
        System.out.println("Generated test map");
    }

    public Cell getCell(int x, int y) {
        if (y >= 0 && y < height && x >= 0 && x < width) {
            return grid[y][x];
        }
        return null;
    }

    public Entity getEntityAt(int x, int y) {
        Cell cell = getCell(x, y);
        return cell != null ? cell.getEntity() : null;
    }

    public boolean moveRobot(Robot robot, int newX, int newY) {
        Cell currentCell = getCell(robot.getX(), robot.getY());
        Cell newCell = getCell(newX, newY);
        if (currentCell == null || newCell == null) {
            System.out.println("Invalid move for " + robot.getName() + ": out of bounds at (" + newX + ", " + newY + ")");
            return false;
        }
        Entity targetEntity = newCell.getEntity();
        if (targetEntity == null) {
            currentCell.removeEntity();
            newCell.setEntity(robot);
            robot.setPosition(newX, newY);
            System.out.println(robot.getName() + " moved to (" + newX + ", " + newY + ")");
            return true;
        } else if (targetEntity instanceof StandardMine) {
            // برخورد با مین
            StandardMine mine = (StandardMine) targetEntity;
            mine.applyEffect(robot); // فرض می‌کنیم مین آسیب می‌زنه
            currentCell.removeEntity();
            newCell.removeEntity(); // حذف مین بعد از انفجار
            robot.setPosition(newX, newY);
            newCell.setEntity(robot);
            System.out.println(robot.getName() + " hit mine at (" + newX + ", " + newY + "), health: " + robot.getHealth());
            return true;
        }
        System.out.println(robot.getName() + " cannot move to (" + newX + ", " + newY + "): cell occupied by " + targetEntity.getClass().getSimpleName());
        return false;
    }

    public boolean shoot(Robot shooter, int targetX, int targetY) {
        // چک کردن مسیر شلیک
        if (!isPathClear(shooter.getX(), shooter.getY(), targetX, targetY)) {
            System.out.println(shooter.getName() + " cannot shoot to (" + targetX + ", " + targetY + "): path blocked");
            return false;
        }
        Entity target = getEntityAt(targetX, targetY);
        if (target != null) {
            if (target instanceof Robot) {
                Robot targetRobot = (Robot) target;
                targetRobot.takeDamage(shooter.getDamage());
                System.out.println(shooter.getName() + " shot " + targetRobot.getName() + " at (" + targetX + ", " + targetY + "), health: " + targetRobot.getHealth());
                return true;
            } else if (target instanceof Obstacle) {
                ((Obstacle) target).applyEffect(shooter);
                if (target instanceof WoodenWall) {
                    getCell(targetX, targetY).removeEntity();
                    System.out.println(shooter.getName() + " destroyed WoodenWall at (" + targetX + ", " + targetY + ")");
                    return true;
                } else if (target instanceof SteelWall) {
                    SteelWall steelWall = (SteelWall) target;
                    steelWall.hit();
                    if (steelWall.isDestroyed()) {
                        getCell(targetX, targetY).removeEntity();
                        System.out.println(shooter.getName() + " destroyed SteelWall at (" + targetX + ", " + targetY + ")");
                        return true;
                    }
                } else if (target instanceof StandardMine) {
                    getCell(targetX, targetY).removeEntity();
                    System.out.println(shooter.getName() + " destroyed mine at (" + targetX + ", " + targetY + ")");
                    return true;
                }
            }
        }
        System.out.println(shooter.getName() + " shot at empty cell (" + targetX + ", " + targetY + ")");
        return false;
    }

    private boolean isPathClear(int startX, int startY, int targetX, int targetY) {
        int dx = targetX - startX;
        int dy = targetY - startY;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        int stepX = dx == 0 ? 0 : (dx > 0 ? 1 : -1);
        int stepY = dy == 0 ? 0 : (dy > 0 ? 1 : -1);
        int x = startX;
        int y = startY;
        for (int i = 1; i < steps; i++) { // تا قبل از هدف
            x += stepX;
            y += stepY;
            if (x < 0 || x >= width || y < 0 || y >= height || getEntityAt(x, y) != null) {
                return false;
            }
        }
        return true;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public static class Cell {
        private int x, y;
        private Entity entity;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Entity getEntity() { return entity; }
        public void setEntity(Entity entity) { this.entity = entity; }
        public void removeEntity() { this.entity = null; }
        public boolean isEmpty() { return entity == null; }
    }
}