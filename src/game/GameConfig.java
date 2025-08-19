package game;

public class GameConfig {
    private int mapWidth = 10;
    private int mapHeight = 10;
    private int robotCount = 4;
    private boolean uiEnabled = true;
    private int mineCount = 5;
    private int normalWallCount = 3;
    private int woodenWallCount = 2;
    private int steelWallCount = 2;

    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public int getRobotCount() { return robotCount; }
    public boolean isUIEnabled() { return uiEnabled; }
    public int getMineCount() { return mineCount; }
    public int getNormalWallCount() { return normalWallCount; }
    public int getWoodenWallCount() { return woodenWallCount; }
    public int getSteelWallCount() { return steelWallCount; }

    public void setMapWidth(int mapWidth) { this.mapWidth = mapWidth; }
    public void setMapHeight(int mapHeight) { this.mapHeight = mapHeight; }
    public void setRobotCount(int robotCount) { this.robotCount = robotCount; }
    public void setUIEnabled(boolean uiEnabled) { this.uiEnabled = uiEnabled; }
    public void setMineCount(int mineCount) { this.mineCount = mineCount; }
    public void setNormalWallCount(int normalWallCount) { this.normalWallCount = normalWallCount; }
    public void setWoodenWallCount(int woodenWallCount) { this.woodenWallCount = woodenWallCount; }
    public void setSteelWallCount(int steelWallCount) { this.steelWallCount = steelWallCount; }
}