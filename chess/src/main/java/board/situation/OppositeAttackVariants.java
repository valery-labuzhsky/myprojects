package board.situation;

import board.pieces.Piece;

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
            friend.getAttacks(attack.move.to).forEach(m -> solutions.add(new Solution(m, this)));
            // TODO let's assume it's 100%, I'll check other variants later
        }
    }

    @Override
    public int getScore() {
        return attack.getScore();
    }

    @Override
    public String toString() {
        return "Attack " + attack.move + " on " + attack.piece + " = " + attack.getScore();
    }

}
