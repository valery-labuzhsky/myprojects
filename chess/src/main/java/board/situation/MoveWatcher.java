package board.situation;

import board.Move;
import board.pieces.Piece;

import java.util.stream.Stream;

/**
 * Created on 07.06.2020.
 *
 * @author unicorn
 */
public abstract class MoveWatcher {
    protected final Move move;
    protected final int color;

    public MoveWatcher(Move move) {
        this.move = move;
        Piece piece = move.piece;
        this.color = piece.color;
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

    public void collect(Stream<Piece> stream) {
        stream.forEach(this::collect);
    }

    public abstract void collect(Piece piece);
}
