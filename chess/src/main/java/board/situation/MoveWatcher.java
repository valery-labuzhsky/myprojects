package board.situation;

import board.Action;
import board.pieces.ScoreProvider;

import java.util.stream.Stream;

/**
 * Created on 07.06.2020.
 *
 * @author unicorn
 */
public abstract class MoveWatcher<A extends Action> {
    protected final A move;

    public MoveWatcher(A move) {
        this.move = move;
    }

    public void calculate() {
        collectBefore();
        move.imagine();
        collectAfter();
        calculateAfter();
        move.undo();
        calculateBefore();
    }

    public abstract void collectBefore();

    public abstract void collectAfter();

    public abstract void calculateBefore();

    public abstract void calculateAfter();

    public void collect(Stream<? extends ScoreProvider> stream) {
        stream.forEach(this::collect);
    }

    public abstract void collect(ScoreProvider piece);
}
