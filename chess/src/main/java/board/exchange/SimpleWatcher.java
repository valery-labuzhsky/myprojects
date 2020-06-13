package board.exchange;

import board.Action;
import board.pieces.ScoreProvider;
import board.situation.MoveWatcher;
import board.situation.ScoreWatcher;

/**
 * Created on 13.06.2020.
 *
 * @author unicorn
 */
public abstract class SimpleWatcher<A extends Action> extends MoveWatcher<A> {
    final ScoreWatcher score;

    public SimpleWatcher(A move) {
        super(move);
        score = new ScoreWatcher();
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
    public void collect(ScoreProvider piece) {
        score.collect(piece);
    }

    public int score() {
        calculate();
        return score.score;
    }
}
