package board.roles;

import board.pieces.Piece;

/**
 * Created on 17.07.2020.
 *
 * @author unicorn
 */
public abstract class Role {
    final Piece piece;

    public Role(Piece piece) {
        this.piece = piece;
    }

    public abstract int getScore();

    public abstract Piece getWhom();
}