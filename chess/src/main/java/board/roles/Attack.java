package board.roles;

import board.exchange.Exchange;
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

    @Override
    public int getScore() {
        Exchange now = whom.getExchange();
        if (nonsense(now)) {
            return 0;
        }
        Exchange then = then(now);
        return then.getScore() - now.getScore();
    }

    protected Exchange then(Exchange now) {
        return now.remove(piece);
    }

    protected abstract boolean nonsense(Exchange now);

    @Override
    public String toString() {
        return piece + " " + verb() + " " + whom + " = " + getScore();
    }

    protected abstract String verb();
}
