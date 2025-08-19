
package game;

public class ScoreManager {
    private int score = 0;

    public void addScoreForObstacleDestroy() {
        score += 10;
    }

    public void addScoreForRobotKill() {
        score += 20;
    }
}

