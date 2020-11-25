package board.situation;

import board.Logged;
import board.Move;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public class Tempo {
    final Move move;
    private final HashSet<MoveProblem> achieves = new HashSet<>();
    final HashSet<Problem> solves = new HashSet<>();

    private Tempo(Move move) {
        this.move = move;
    }

    static void achieves(HashMap<Move, Tempo> tempos, CaptureVariantProblem p) {
        compute(tempos, p.move).achieves(p);
    }

    static void solves(HashMap<Move, Tempo> tempos, Solution solution) {
        compute(tempos, solution.move).solves(solution.problem);
    }

    private static Tempo compute(HashMap<Move, Tempo> tempos, Move move) {
        return tempos.compute(move, (m, t) -> t == null ? new Tempo(move) : t);
    }

    private void achieves(MoveProblem problem) {
        achieves.add(problem);
    }

    public Tempo solves(Problem problem) {
        solves.add(problem);
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
