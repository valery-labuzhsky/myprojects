package board.situation;

import board.Logged;

import java.util.HashSet;

/**
 * Created on 07.06.2020.
 *
 * @author unicorn
 */
public class ListScoreWatcher {
    final HashSet<ScoreDiff> diffs = new HashSet<>();
    private int score;

    public ListScoreWatcher() {
    }

    public void collect(ScoreDiff diff) {
        diffs.add(diff);
    }

    public void before() {
        diffs.forEach(ScoreDiff::before);
    }

    public void after() {
        diffs.forEach(ScoreDiff::after);
    }

    public int getScore() {
        return score;
    }

    public void calculate() {
        diffs.removeIf(diff -> diff.getScore() == 0 && diff.getAfter() == 0);
        score = diffs.stream().mapToInt(ScoreDiff::getScore).sum();
    }

    @Override
    public String toString() {
        return "" + Logged.tabs(diffs);
    }

}
