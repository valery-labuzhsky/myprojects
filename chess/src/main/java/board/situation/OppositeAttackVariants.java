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
public class OppositeAttackVariants extends Problem {
    private final AfterMoveScore attack;

    OppositeAttackVariants(AfterMoveScore attack) {
        this.attack = attack;

        Piece attacked = attack.piece;
        for (Piece friend : attacked.board.pieces.get(attacked.color)) {
            friend.getAttacks(attack.move.to).forEach(this::checkSolution);
        }
    }

    private void checkSolution(Move move) {
        int color = attack.piece.color;
        int score = new Exchange(move.to, -color).move(move.piece).getScore() * color;
        if (score >= 0) {
            solutions.add(new Solution(move, this));
        }
    }

    public OppositeAttackVariants counterAttacks(ArrayList<AfterMoveScore> attacks) {
        int myColor = attack.piece.color;
        for (AfterMoveScore attack : attacks) {
            if (attack.getScore() * myColor >= getScore() * myColor) {
                if (this.attack.move.piece != attack.piece) {
                    solutions.add(new Solution(attack.move, this));
                }
            }
        }
        return this;
    }

    @Override
    public int getScore() {
        return attack.getScore();
    }

    @Override
    public String toString() {
        return attack.toString();
    }

}
