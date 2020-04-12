package board;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Rook extends Piece {
    public Rook(Board board, int color) {
        super(PieceType.Rook, board, color);
    }

    @Override
    protected void marksOn() {
        markLine(0, 1);
        markLine(1, 0);
        markLine(0, -1);
        markLine(-1, 0);
    }
}
