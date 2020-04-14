package board;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Bishop extends Piece {
    public Bishop(Board board, int color) {
        super(PieceType.Bishop, board, color);
    }

    @Override
    protected void marksOn(Waypoint.Origin origin) {
        origin.markLine(1, 1);
        origin.markLine(1, -1);
        origin.markLine(-1, -1);
        origin.markLine(-1, 1);
    }

}
