
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


/*
package game;

public class ScoreManager {
    private int score;

    public ScoreManager() {
        this.score = 0;
    }

    public void addScoreForRobotKill() {
        score += 50; // 50 امتیاز برای هر ربات نابودشده
        System.out.println("امتیاز برای نابود کردن ربات اضافه شد! امتیاز فعلی: " + score);
    }

    public void addScoreForObstacleDestroy() {
        score += 20; // 20 امتیاز برای هر مانع نابودشده
        System.out.println("امتیاز برای نابود کردن مانع اضافه شد! امتیاز فعلی: " + score);
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
        System.out.println("امتیازات ریست شد!");
    }
}*/