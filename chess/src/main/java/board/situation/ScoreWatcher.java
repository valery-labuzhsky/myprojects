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
    private int before;
    private int after;

    public ScoreWatcher(ScoreProvider provider) {
        this.provider = provider;
    }

    @Override
    public void before() {
        before = provider.getScore();
    }

    @Override
    public void after() {
        after = provider.getScore();
    }

    @Override
    public int getBefore() {
        return before;
    }

    @Override
    public int getAfter() {
        return after;
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
        return provider + ":" + getAfter();
    }
}
