package board.roles;

import board.exchange.Exchange;
import board.pieces.Piece;

/**
 * Created on 17.07.2020.
 *
 * @author unicorn
 */
public abstract class Target extends Role {
    public final Piece whom;

    Target(Piece piece, Piece whom) {
        super(piece);
        this.whom = whom;
    }

    public static Target create(Piece piece, Piece whom) {
        if (piece.color == whom.color) {
            return new Protection(piece, whom);
        } else {
            return new Attack(piece, whom);
        }
    }

    @Override
    public int getScore() {
        Exchange now = new Exchange(whom.square, -whom.color);
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
    public Piece getWhom() {
        return whom;
    }

    protected abstract String verb();

    @Override
    public String toString() {
        return piece + " " + verb() + " " + whom + " = " + getScore();
    }
}
