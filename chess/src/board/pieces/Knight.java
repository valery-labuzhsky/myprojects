package board.pieces;

import board.Waypoint;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Knight extends Piece {
    public Knight(Board board, int color) {
        super(PieceType.Knight, board, color);
    }

    @Override
    public void marksOn(Waypoint.Origin origin) {
        origin.mark(1, 2);
        origin.mark(-1, 2);
        origin.mark(2, 1);
        origin.mark(2, -1);
        origin.mark(1, -2);
        origin.mark(-1, -2);
        origin.mark(-2, 1);
        origin.mark(-2, -1);
    }
}
