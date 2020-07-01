package board.exchange;

import board.Action;
import board.pieces.Piece;
import board.situation.ListScoreWatcher;
import board.situation.MoveScore;
import board.situation.ScoreDiff;
import board.situation.ScoreWatcher;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created on 13.06.2020.
 *
 * @author unicorn
 */
public abstract class DiffMoveScore<A extends Action> extends MoveScore<A> {
    protected final ListScoreWatcher score;
    protected final Function<Piece, ScoreWatcher> diff;
    private boolean calculated;

    public DiffMoveScore(A move, Function<Piece, ScoreWatcher> diff) {
        super(move);
        score = new ListScoreWatcher();
        this.diff = diff;
    }

    @Override
    protected void collectAfter() {
    }

    @Override
    protected void calculateBefore() {
        score.before();
    }

    @Override
    protected void calculateAfter() {
        score.after();
    }

    @Override
    protected void finish() {
        score.calculate();
    }

    public int getScore() {
        if (!calculated) {
            calculate();
            calculated = true;
        }
        return score.getScore();
    }

    public String toString() {
        return "" + move + "=" + getScore() + ": " + score;
    }

    private void diffs(Stream<? extends ScoreDiff> stream) {
        stream.forEach(score::collect);
    }

    protected void myColor(Stream<Piece> pieces) {
        int color = move.piece.color;
        pieces(pieces.filter(p -> p.color == color));
    }

    protected void oppositeColor(Stream<Piece> stream) {
        int color = move.piece.color;
        pieces(stream.filter(p -> p.color != color));
    }

    public void pieces(Stream<Piece> pieces) {
        diffs(pieces.map(diff));
    }

}
