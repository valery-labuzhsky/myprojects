package board.situation;

import board.Move;

import java.util.ArrayList;

/**
 * Created on 25.11.2020.
 *
 * @author unicorn
 */
public abstract class ProblemSolver {
    final Problem problem;
    final ArrayList<Solution> solutions = new ArrayList<>();

    public ProblemSolver(Problem problem) {
        this.problem = problem;
    }

    void counterAttacks(ArrayList<AttackProblem> attacks) {
        int myColor = problem.piece.color;
        for (AttackProblem attack : attacks) {
            if (attack.getScore() * myColor >= -getScore() * myColor) {
                solutions.add(new Solution("Counterattack", attack.move, problem));
            }
        }
    }

    public abstract int getScore();

    @Deprecated
    protected void addSolution(Move move) {
        solutions.add(new Solution(move, problem));
    }

    protected void addSolution(String name, Move move) {
        solutions.add(new Solution(name, move, problem));
    }

    abstract void captures(ArrayList<CaptureVariantProblem> captures);
}
