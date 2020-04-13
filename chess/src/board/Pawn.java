package board;

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
    protected void marksOn() {
        Waypoint first = mark(null, 0, color);
        mark(-1, color);
        mark(1, color);
        // enpassant
        int border = (7 - color * 7) / 2;
        int secondRow = border + color;
        if (square.pair.rank == secondRow) {
            mark(first, 0, color);
        }
    }

    @Override
    public boolean goes(Waypoint waypoint) {
        if (waypoint.square.pair.file == square.pair.file) {
            if (waypoint.square.piece != null) {
                return false;
            }
        } else {
            // TODO enpassant
            if (waypoint.square.piece == null) {
                return false;
            }
        }
        return super.goes(waypoint);
    }

    @Override
    public boolean captures(Waypoint waypoint) {
        return waypoint.square.pair.file != square.pair.file;
    }
}
