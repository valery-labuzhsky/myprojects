package board.roles;

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
    public boolean isMeaningful() {
        return true;
    }

    @Override
    public String toString() {
        return piece + " threatens " + whom;
    }

    @Override
    public String toContinuous() {
        return piece + " threatening " + whom;
    }
}
