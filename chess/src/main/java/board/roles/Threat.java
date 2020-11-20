package board.roles;

import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 18.07.2020.
 *
 * @author unicorn
 */
public class Threat extends Attack {
    public Threat(Piece piece, Piece whom) {
        super(piece, whom);
    }

    @Override
    protected boolean nonsense(Exchange now) {
        return now.getScore() == 0;
    }

    @Override
    protected String verb() {
        return "threatens";
    }
}