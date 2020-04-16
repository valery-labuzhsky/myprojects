package board.pieces;

import board.Waypoint;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class King extends Piece {
    public King(Board board, int color) {
        super(PieceType.King, board, color);
    }

    @Override
    public void marksOn(Waypoint.Origin origin) {
        origin.mark(0, 1);
        origin.mark(1, 1);
        origin.mark(1, 0);
        origin.mark(1, -1);
        origin.mark(0, -1);
        origin.mark(-1, -1);
        origin.mark(-1, 0);
        origin.mark(-1, 1);
        // TODO castling
    }
}
