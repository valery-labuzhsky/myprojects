package board.roles;

import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 18.07.2020.
 *
 * @author unicorn
 */
public class Protection extends Target {
    public Protection(Piece piece, Piece whom) {
        super(piece, whom);
    }

    @Override
    protected boolean nonsense(Exchange now) {
        return now.sides.get(-whom.color).pieces.isEmpty() || now.getScore() == whom.cost();
    }

    @Override
    protected String verb() {
        return "protects";
    }
}
