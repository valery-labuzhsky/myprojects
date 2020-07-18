package board.roles;

import board.pieces.Piece;

/**
 * Created on 17.07.2020.
 *
 * @author unicorn
 */
public abstract class Attack extends Role {
    public final Piece whom;

    Attack(Piece piece, Piece whom) {
        super(piece);
        this.whom = whom;
    }

    public static Attack create(Piece piece, Piece whom) {
        if (piece.color == whom.color) {
            return new Protection(piece, whom);
        } else {
            return new Threat(piece, whom);
        }
    }

    public abstract String toContinuous();
}
