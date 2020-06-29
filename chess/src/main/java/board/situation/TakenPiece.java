package board.situation;

import board.pieces.Piece;

/**
 * Created on 28.06.2020.
 *
 * @author unicorn
 */
public class TakenPiece implements Analytics {
    private final Piece piece;

    public TakenPiece(Piece piece) {
        this.piece = piece;
    }

    @Override
    public int getScore() {
        return 0;
    }

    @Override
    public String toString() {
        return "x" + piece;
    }
}
