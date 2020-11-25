package board.situation;

import java.util.ArrayList;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public abstract class MoveProblemSolver extends ProblemSolver {

    MoveProblemSolver(MoveProblem problem) {
        super(problem);
    }

    public MoveProblem getProblem() {
        return (MoveProblem) problem;
    }

    @Override
    void captures(ArrayList<CaptureVariantProblem> captures) {
        for (CaptureVariantProblem capture : captures) {
            if (capture.piece == getProblem().move.piece) {
                solutions.add(capture.solves(problem));
            }
        }
    }

}
