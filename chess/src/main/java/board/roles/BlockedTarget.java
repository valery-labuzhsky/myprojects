package board.roles;

import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 23.07.2020.
 *
 * @author unicorn
 */
public abstract class BlockedTarget extends Target {
    BlockedTarget(Piece piece, Piece whom) {
        super(piece, whom);
    }

    public static BlockedTarget create(Piece piece, Piece whom) {
        if (piece.color == whom.color) {
            return new BlockedProtection(piece, whom);
        } else {
            return new BlockedAttack(piece, whom);
        }
    }

    @Override
    protected Exchange then(Exchange now) {
        return now.add(piece);
    }

}
