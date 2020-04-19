package board.pieces;

import board.MovesTracer;

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
    public void trace(MovesTracer tracer) {
        tracer.go(0, 1);
        tracer.go(1, 1);
        tracer.go(1, 0);
        tracer.go(1, -1);
        tracer.go(0, -1);
        tracer.go(-1, -1);
        tracer.go(-1, 0);
        tracer.go(-1, 1);
        // TODO castling
    }
}
