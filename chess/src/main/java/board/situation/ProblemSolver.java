package board.situation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public abstract class ProblemSolver {
    final Problem problem;
    private final ArrayList<Solution> solutions = new ArrayList<>();

    ProblemSolver(Problem problem) {
        this.problem = problem;
    }

    void counterAttacks(List<AfterMoveScore> attacks) {
        int myColor = problem.piece.color;
        for (AfterMoveScore attack : attacks) {
            if (problem.move.piece != attack.piece) {
                if (attack.getScore() * myColor >= -getScore() * myColor) {
                    getSolutions().add(new Solution("Counterattack", attack.move, problem));
                }
            }
        }
    }

    void captures(ArrayList<Solution> captures) {
        for (Solution capture : captures) {
            if (capture.problem.piece == problem.move.piece) {
                getSolutions().add(capture.fitMeToo(problem));
            }
        }
    }

    public abstract int getScore();

    ArrayList<Solution> getSolutions() {
        return solutions;
    }

}
