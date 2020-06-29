package board.situation;

import board.Action;
import board.Logged;

/**
 * Created on 07.06.2020.
 *
 * @author unicorn
 */
public abstract class MoveCalculator<A extends Action> {
    protected final A move;

    public MoveCalculator(A move) {
        this.move = move;
    }

    public void calculate() {
        move.stack(() -> {
            Logged.log("bfr").stack(this::collectBefore);
            move.imagine();
            Logged.log("aft").stack(() -> {
                collectAfter();
                calculateAfter();
            });
            move.undo();
            Logged.log("bfr").stack(this::calculateBefore);
            finish();
        });
    }

    public abstract void collectBefore();

    public abstract void collectAfter();

    public abstract void calculateBefore();

    public abstract void calculateAfter();

    public abstract void finish();
}
