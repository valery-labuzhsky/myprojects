package board.situation;

import board.Move;

import java.util.ArrayList;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public abstract class ProblemSolver {
    final Problem problem;
    final ArrayList<Solution> solutions = new ArrayList<>();

    ProblemSolver(Problem problem) {
        this.problem = problem;
    }

    void counterAttacks(ArrayList<AttackProblem> attacks) {
        int myColor = problem.piece.color;
        for (AttackProblem attack : attacks) {
            if (problem.move.piece != attack.piece) {
                if (attack.getScore() * myColor >= -getScore() * myColor) {
                    solutions.add(new Solution("Counterattack", attack.move, problem));
                }
            }
        }
    }

    void captures(ArrayList<CaptureProblem> captures) {
        for (CaptureProblem capture : captures) {
            if (capture.piece == problem.move.piece) {
                solutions.add(capture.solves(problem));
            }
        }
    }

    public abstract int getScore();

    protected void addSolution(Move move) {
        solutions.add(new Solution(move, problem));
    }
}
