package board.situation;

import board.pieces.ScoreProvider;

/**
 * Created on 07.06.2020.
 *
 * @author unicorn
 */
public class ScoreWatcher {
    final ScoreProviders providers = new ScoreProviders();
    public int score;

    public ScoreWatcher() {
    }

    public void before() {
        score -= this.providers.getScore();
    }

    public void after() {
        score += this.providers.getScore();
    }

    public void collect(ScoreProvider provider) {
        this.providers.add(provider);
    }

    public void exclude(ScoreProvider provider) {
        this.providers.remove(provider);
    }
}
