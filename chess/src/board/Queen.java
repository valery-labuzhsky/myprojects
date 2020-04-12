package board;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Queen extends Piece {
    public Queen(Board board, int color) {
        super(PieceType.Queen, board, color);
    }

    @Override
    protected void marksOn() {
        markLine(0, 1);
        markLine(1, 1);
        markLine(1, 0);
        markLine(1, -1);
        markLine(0, -1);
        markLine(-1, -1);
        markLine(-1, 0);
        markLine(-1, 1);
    }

}
