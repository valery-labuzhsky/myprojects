package patrician.save;

/**
 * Created on 15/03/15.
 *
 * @author ptasha
 */
public enum Direction {
    SELL(true, false),
    BUY(false, true),
    ALL(true, true);


    private final boolean sell;
    private final boolean buy;

    Direction(boolean sell, boolean buy) {
        this.sell = sell;
        this.buy = buy;
    }

    public boolean isSell() {
        return sell;
    }

    public boolean isBuy() {
        return buy;
    }
}
