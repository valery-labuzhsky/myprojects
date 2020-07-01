package board.situation;

import board.Action;
import board.Logged;

import java.util.Objects;

/**
 * Created on 07.06.2020.
 *
 * @author unicorn
 */
public abstract class MoveScore<A extends Action> {
    protected final A move;

    public MoveScore(A move) {
        this.move = move;
    }

    protected void calculate() {
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

    protected abstract void collectBefore();

    protected abstract void collectAfter();

    protected abstract void calculateBefore();

    protected abstract void calculateAfter();

    protected abstract void finish();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveScore<?> that = (MoveScore<?>) o;
        return move.equals(that.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move);
    }
}
