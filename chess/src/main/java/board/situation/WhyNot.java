package board.situation;

import board.Logged;

import java.util.List;

/**
 * Created on 20.11.2020.
 *
 * @author unicorn
 */
public class WhyNot implements Analytics {
    private final List<Analytics> whyNots;

    WhyNot(List<Analytics> whyNots) {
        this.whyNots = whyNots;
    }

    @Override
    public int getScore() {
        return whyNots.stream().mapToInt(e -> e.getScore()).sum();
    }

    @Override
    public String toString() {
        return Logged.tabs("Why Not", whyNots);
    }
}
