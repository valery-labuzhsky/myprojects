package board.situation;

import board.Move;
import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 04.07.2020.
 *
 * @author unicorn
 */
public class AttackProblemSolver extends ProblemSolver {
    AttackProblemSolver(AttackProblem attack) {
        super(attack);

        Piece attacked = problem.piece;
        for (Piece friend : attacked.board.pieces.get(attacked.color)) {
            friend.planAttacks(problem.move.to).forEach(this::checkSolution);
        }
    }

    private void checkSolution(Move move) {
        int color = problem.piece.color;
        int score = new Exchange(move.to, -color).move(move.piece).getScore() * color;
        if (score >= 0) {
            addSolution(move);
        }
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
