package board.roles;

import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 23.07.2020.
 *
 * @author unicorn
 */
public class BlockedProtection extends BlockedAttack {
    BlockedProtection(Piece piece, Piece whom) {
        super(piece, whom);
    }

    @Override
    protected boolean nonsense(Exchange now) {
        return now.getScore() == 0;
    }

    @Override
    protected String verb() {
        return "protecting";
    }
}
