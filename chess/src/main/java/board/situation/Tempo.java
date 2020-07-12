package board.situation;

import board.Logged;
import board.Move;

import java.util.HashSet;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public class Tempo {
    final Move move;
    private final HashSet<Problem> achieves = new HashSet<>();
    final HashSet<Problem> solves = new HashSet<>();

    Tempo(Solution solution) {
        move = solution.move;
        add(solution);
    }

    public Tempo add(Solution solution) {
        if (move.equals(solution.problem.move)) {
            achieves.add(solution.problem);
        } else {
            solves.add(solution.problem);
        }
        return this;
    }

    public int getScore() {
        return achieves.stream().mapToInt(p -> p.getScore()).sum() - solves.stream().mapToInt(p -> p.getScore()).sum();
    }

    @Override
    public String toString() {
        return "" + move + ": " +
                Logged.shortTabs("Solves", solves) +
                Logged.shortTabs("Achieves", achieves);
    }
}
