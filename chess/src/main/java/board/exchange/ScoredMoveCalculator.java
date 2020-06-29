package board.exchange;

import board.Action;
import board.pieces.Piece;
import board.situation.ListScoreWatcher;
import board.situation.MoveCalculator;
import board.situation.ScoreDiff;
import board.situation.ScoreWatcher;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created on 13.06.2020.
 *
 * @author unicorn
 */
public abstract class ScoredMoveCalculator<A extends Action> extends MoveCalculator<A> {
    protected final ListScoreWatcher score;
    protected final Function<Piece, ScoreWatcher> diff;

    public ScoredMoveCalculator(A move, Function<Piece, ScoreWatcher> diff) {
        super(move);
        score = new ListScoreWatcher();
        this.diff = diff;
    }

    @Override
    public void collectAfter() {
    }

    @Override
    public void calculateBefore() {
        score.before();
    }

    @Override
    public void calculateAfter() {
        score.after();
    }

    @Override
    public void finish() {
        score.calculate();
    }

    public int score() {
        calculate();
        return getScore();
    }

    public int getScore() {
        return score.getScore();
    }

    public String toString() {
        return "" + move + "=" + score.getScore() + ": " + score;
    }

    public void diffs(Stream<? extends ScoreDiff> stream) {
        stream.forEach(score::collect);
    }

    public void myColor(Stream<Piece> pieces) {
        int color = move.piece.color;
        pieces(pieces.filter(p -> p.color == color));
    }

    public void oppositeColor(Stream<Piece> stream) {
        int color = move.piece.color;
        pieces(stream.filter(p -> p.color != color));
    }

    public void pieces(Stream<Piece> pieces) {
        diffs(pieces.map(diff));
    }

}
