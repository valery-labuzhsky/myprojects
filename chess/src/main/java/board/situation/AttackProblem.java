package board.situation;

import board.Move;
import board.pieces.Piece;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public class AttackProblem extends Problem {
    private final AfterMoveScore attack;
    final Piece piece;
    final Move move;

    public AttackProblem(AfterMoveScore attack) {
        this.attack = attack;
        piece = attack.piece;
        move = attack.move;
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
