package board.roles;

import board.pieces.Piece;

/**
 * Created on 18.07.2020.
 *
 * @author unicorn
 */
public class Protection extends Attack {
    public Protection(Piece piece, Piece whom) {
        super(piece, whom);
    }

    @Override
    public boolean isMeaningful() {
        return !whom.getExchange().sides.get(-whom.color).pieces.isEmpty();
    }

    @Override
    public String toString() {
        return piece + " protects " + whom;
    }

    @Override
    public String toContinuous() {
        return piece + " protecting " + whom;
    }
}
