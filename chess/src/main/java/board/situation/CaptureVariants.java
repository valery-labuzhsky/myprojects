package board.situation;

import board.Waypoint;
import board.pieces.Piece;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class CaptureVariants extends Variants {

    CaptureVariants(Piece piece) {
        super(piece, -piece.color);
        solve();
    }

    private void solve() {
        for (Waypoint waypoint : this.square.getWaypoints()) {
            if (waypoint.captures()) {
                addSolution(waypoint.move());
            }
        }
    }

    public int score() {
        return 0;
    }
}
