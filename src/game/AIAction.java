package game;

/**
 * این کلاس یک اقدام هوش مصنوعی را مشخص می‌کند، شامل نوع (حرکت، حمله، انتظار) و هدف.
 */
public class AIAction {

    public enum ActionType {
        MOVE, ATTACK, WAIT
    }

    private ActionType type;
    private Object target; // Point برای حرکت، AIPlayer برای حمله

    public AIAction(ActionType type, Object target) {
        this.type = type;
        this.target = target;
    }

    public ActionType getType() {
        return type;
    }

    public Object getTarget() {
        return target;
    }
}
