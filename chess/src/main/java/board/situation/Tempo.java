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
    final Move move;
    // TODO it's better to have not score but positional knowledge
    //  it should me a sum of problems it creates
    //  plus score for capturing a piece
    private int additional;
    final ArrayList<Problem> problems = new ArrayList<>();

    Tempo(Solution solution) {
        move = solution.move;
        add(solution);
    }

    public Tempo add(Solution solution) {
        if (solution.problem != null) {
            problems.add(solution.problem);
        }
        additional += solution.getAdditional();
        return this;
    }

    public int getScore() {
        return additional - problems.stream().mapToInt(p -> p.getScore()).sum();
    }

    @Override
    public String toString() {
        return "" + move + ": " + additional + Logged.shortTabs("Problems", problems);
    }
}
