package board.situation;

import board.pieces.ScoreProvider;

import java.util.Objects;

/**
 * Created on 21.06.2020.
 *
 * @author unicorn
 */
public class ScoreWatcher implements ScoreDiff {
    final ScoreProvider provider;
    private Analytics before;
    private Analytics after;

    public ScoreWatcher(ScoreProvider provider) {
        this.provider = provider;
    }

    @Override
    public void before() {
        before = provider.analyse();
    }

    @Override
    public void after() {
        after = provider.analyse();
    }

    @Override
    public int getBefore() {
        return before.getScore();
    }

    @Override
    public int getAfter() {
        return after.getScore();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScoreWatcher watcher = (ScoreWatcher) o;
        return provider.equals(watcher.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(provider);
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        if (before.getScore() != 0) {
            string.append("Before (").append(before.getScore()).append(") ").append(before);
        }
        if (after.getScore() != 0) {
            if (string.length() > 0) {
                string.append("\n");
            }
            string.append("After (").append(after.getScore()).append(") ").append(after);
        }
        return string.length() == 0 ? "No change" : string.toString();
    }
}
