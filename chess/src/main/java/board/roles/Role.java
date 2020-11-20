package board.roles;

import board.pieces.Piece;
import board.situation.Analytics;

/**
 * Created on 17.07.2020.
 *
 * @author unicorn
 */
public abstract class Role implements Analytics {
    final Piece piece;

    public Role(Piece piece) {
        this.piece = piece;
    }

    public abstract Piece getWhom();
}
