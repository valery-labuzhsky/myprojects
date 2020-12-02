package board.roles;

import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 23.07.2020.
 *
 * @author unicorn
 */
public class BlockedAttack extends BlockedTarget {
    BlockedAttack(Piece piece, Piece whom) {
        super(piece, whom);
    }

    @Override
    protected boolean nonsense(Exchange now) {
        return now.getScore() == whom.cost();
    }

    @Override
    protected String verb() {
        return "attacking";
    }
}
