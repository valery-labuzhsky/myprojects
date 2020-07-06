package board.situation;

import board.Logged;
import board.Move;

import java.util.ArrayList;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public class Tempo {
    // TODO it's projection of solution but with multiple problems
    //  it's part of decision making so far not positional assessment
    final Move move;
    private final int negative; // TODO it's better to have not score but positional knowledge
    private final ArrayList<Problem> problems = new ArrayList<>();

    Tempo(Solution solution) {
        negative = solution.getNegative();
        move = solution.move;
        add(solution);
    }

    public Tempo add(Solution solution) {
        problems.add(solution.problem);
        return this;
    }

    public int getScore() {
        return negative - problems.stream().mapToInt(p -> p.getScore()).sum();
    }

    @Override
    public String toString() {
        return "" + move + ": " + negative + Logged.tabs("Problems", problems);
    }
}
