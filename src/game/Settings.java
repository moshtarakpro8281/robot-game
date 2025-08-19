package game;

public class Settings {
    private static BattleMode battleMode = BattleMode.PLAYER_VS_PLAYER; // حالت پیش‌فرض

    public static BattleMode getBattleMode() {
        return battleMode;
    }

    public static void setBattleMode(BattleMode battleMode) {
        if (battleMode != null) {
            Settings.battleMode = battleMode;
            System.out.println("Battle mode set to: " + battleMode); // برای دیباگ
        } else {
            System.out.println("Warning: Attempted to set null battle mode, keeping default: " + Settings.battleMode);
        }
    }

    // متد جدید برای ریست کردن به حالت پیش‌فرض (اختیاری)
    public static void resetBattleMode() {
        battleMode = BattleMode.PLAYER_VS_PLAYER;
        System.out.println("Battle mode reset to default: " + battleMode);
    }
}
