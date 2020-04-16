package board.pieces;

import board.Square;
import board.Waypoint;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Pawn extends Piece {
    public Pawn(Board board, int color) {
        super(PieceType.Pawn, board, color);
    }

    @Override
    public void marksOn(Waypoint.Origin origin) {
        Waypoint first = origin.mark(0, color);
        origin.mark(-1, color);
        origin.mark(1, color);
        // enpassant
        int border = (7 - color * 7) / 2;
        int secondRow = border + color;
        if (square.pair.rank == secondRow) {
            origin.mark(first, 0, color);
        }
    }

    @Override
    public boolean goes(Waypoint waypoint) {
        Square square = waypoint.getOriginalSquare();
        return waypoint.square.pair.file == square.pair.file;
    }

    @Override
    public boolean attacks(Waypoint waypoint) {
        Square square = waypoint.getOriginalSquare();
        return waypoint.square.pair.file != square.pair.file;
    }

}
