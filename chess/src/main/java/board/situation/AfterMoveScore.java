package board.situation;

import board.Move;
import board.pieces.Piece;

/**
 * Created on 04.07.2020.
 *
 * @author unicorn
 */
public class AfterMoveScore extends MoveScore<Move> {
    final Piece piece;
    final ScoreDiff diff;

    public AfterMoveScore(Piece piece, Move move, ScoreDiff diff) {
        super(move);
        this.piece = piece;
        this.diff = diff;
        calculate();
    }

    @Override
    protected void collectBefore() {
    }

    @Override
    protected void collectAfter() {
    }

    @Override
    protected void calculateBefore() {
        diff.before();
    }

    @Override
    protected void calculateAfter() {
        diff.after();
    }

    @Override
    protected void finish() {
    }

    public int getScore() {
        return diff.getScore();
    }

    @Override
    public String toString() {
        return "Attack " + move + " on " + piece + " = " + getScore() + diff;
    }
}
