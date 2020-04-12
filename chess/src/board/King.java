package board;

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
    protected void marksOn() {
        mark(0, 1);
        mark(1, 1);
        mark(1, 0);
        mark(1, -1);
        mark(0, -1);
        mark(-1, -1);
        mark(-1, 0);
        mark(-1, 1);
        // TODO castling
    }

    @Override
    protected Move move(Waypoint waypoint) {
        for (Waypoint m : waypoint.square.waypoints) {
            if (m.captures(this)) {
                return null;
            }
        }
        return super.move(waypoint);
    }

}
