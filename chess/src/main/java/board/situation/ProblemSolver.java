package board.situation;

import java.util.ArrayList;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public abstract class ProblemSolver {
    private final ArrayList<Solution> solutions = new ArrayList<>();

    public abstract int getScore();

    // TODO and I'll have
    //  I have at least 2 solvers for each problem
    //  so I must describe problem separately
    public ArrayList<Solution> getSolutions() {
        return solutions;
    }
}
