package board.situation;

import board.Move;
import board.exchange.DiffMoveScore;
import board.pieces.Piece;

import java.util.function.Function;

/**
 * Created on 04.07.2020.
 *
 * @author unicorn
 */
public class AfterMoveScore extends DiffMoveScore<Move> {
    final Piece piece;

    public AfterMoveScore(Piece piece, Move move, Function<Piece, ScoreWatcher> diff) {
        super(move, diff);
        this.piece = piece;
    }

    @Override
    protected void collectBefore() {
        // TODO it's only one piece
        score.collect(diff.apply(piece));
    }

    @Override
    public String toString() {
        return "Attack " + move + " on " + piece + " = " + getScore() + score;
    }
}
