package board.situation;

import board.pieces.ScoreProvider;

import java.util.HashSet;

/**
 * Created on 07.06.2020.
 *
 * @author unicorn
 */
public class ScoreProviders {
    HashSet<ScoreProvider> scores = new HashSet<>();

    public ScoreProviders() {
    }

    public void add(ScoreProvider provider) {
        this.scores.add(provider);
    }

    public void remove(ScoreProvider provider) {
        this.scores.remove(provider);
    }

    public int getScore() {
        int score = 0;
        for (ScoreProvider provider : this.scores) {
            score += provider.getScore();
        }
        return score;
    }
}
