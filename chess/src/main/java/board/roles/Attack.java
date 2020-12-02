package board.roles;

import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 18.07.2020.
 *
 * @author unicorn
 */
public class Attack extends Target {
    public Attack(Piece piece, Piece whom) {
        super(piece, whom);
    }

    @Override
    protected boolean nonsense(Exchange now) {
        return now.getScore() == 0;
    }

    @Override
    protected String verb() {
        return "attacks"; // attacks is free to use, threatens is too unfamiliar
    }
}
