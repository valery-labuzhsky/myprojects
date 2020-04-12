package board;

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
    protected void marksOn() {
        mark(1, 2);
        mark(-1, 2);
        mark(2, 1);
        mark(2, -1);
        mark(1, -2);
        mark(-1, -2);
        mark(-2, 1);
        mark(-2, -1);
    }
}
