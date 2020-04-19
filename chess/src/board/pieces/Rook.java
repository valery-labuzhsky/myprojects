package board.pieces;

import board.MovesTracer;

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
    public void trace(MovesTracer tracer) {
        tracer.markLine(0, 1);
        tracer.markLine(1, 0);
        tracer.markLine(0, -1);
        tracer.markLine(-1, 0);
    }
}
