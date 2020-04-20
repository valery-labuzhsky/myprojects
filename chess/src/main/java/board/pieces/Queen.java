package board.pieces;

import board.MovesTracer;

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
    public void trace(MovesTracer tracer) {
        tracer.markLine(0, 1);
        tracer.markLine(1, 1);
        tracer.markLine(1, 0);
        tracer.markLine(1, -1);
        tracer.markLine(0, -1);
        tracer.markLine(-1, -1);
        tracer.markLine(-1, 0);
        tracer.markLine(-1, 1);
    }

}
