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
        this(solution.problem);
    }

    Tempo(Problem problem) {
        move = problem.move;
        add(problem);
    }

    public Tempo add(Solution solution) {
        return add(solution.problem);
    }

    public Tempo add(Problem problem) {
        if (move.equals(problem.move)) {
            achieves.add(problem);
        } else {
            solves.add(problem);
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
