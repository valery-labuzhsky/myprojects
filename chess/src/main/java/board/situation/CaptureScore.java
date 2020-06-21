package board.situation;

import board.pieces.Piece;

/**
 * Created on 21.06.2020.
 *
 * @author unicorn
 */
public class CaptureScore implements SimpleScoreDiff {
    private final Piece piece;

    public CaptureScore(Piece piece) {
        this.piece = piece;
    }

    @Override
    public int getAfter() {
        return -piece.cost();
    }

    @Override
    public String toString() {
        return "x" + piece;
    }
}
