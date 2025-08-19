package game;

public class StandardMine extends Mine {
    public StandardMine(int x, int y) {
        super(x, y, ObstacleType.MINE);
    }

    @Override
    public void applyEffect(Robot robot) {
        System.out.println("Mine hit " + robot.getName() + ", dealing 50 damage!");
        robot.takeDamage(50);
    }
}
