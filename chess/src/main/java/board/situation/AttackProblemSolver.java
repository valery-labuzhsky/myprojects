package board.situation;

import board.Move;
import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 04.07.2020.
 *
 * @author unicorn
 */
public class AttackProblemSolver extends ProblemSolver {
    private final AttackProblem problem;

    AttackProblemSolver(AfterMoveScore attack) {
        problem = new AttackProblem(attack);

        Piece attacked = problem.piece;
        for (Piece friend : attacked.board.pieces.get(attacked.color)) {
            friend.getAttacks(problem.move.to).forEach(this::checkSolution);
        }
    }

    private void checkSolution(Move move) {
        int color = problem.piece.color;
        int score = new Exchange(move.to, -color).move(move.piece).getScore() * color;
        if (score >= 0) {
            getSolutions().add(new Solution(move, problem));
        }
    }

    public AttackProblemSolver counterAttacks(ArrayList<AfterMoveScore> attacks) {
        int myColor = problem.piece.color;
        for (AfterMoveScore attack : attacks) {
            if (attack.getScore() * myColor >= getScore() * myColor) {
                if (problem.move.piece != attack.piece) {
                    getSolutions().add(new Solution(attack.move, problem));
                }
            }
        }
        return this;
    }

    @Override
    public int getScore() {
        return problem.getScore();
    }

    @Override
    public String toString() {
        return problem.toString();
    }

}
