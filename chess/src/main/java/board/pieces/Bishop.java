package board.pieces;

import board.MovesTracer;

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
    public void trace(MovesTracer tracer) {
        tracer.markLine(1, 1);
        tracer.markLine(1, -1);
        tracer.markLine(-1, -1);
        tracer.markLine(-1, 1);
    }

}
