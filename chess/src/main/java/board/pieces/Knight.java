package board.pieces;

import board.MovesTracer;

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
    public void trace(MovesTracer tracer) {
        tracer.go(1, 2);
        tracer.go(-1, 2);
        tracer.go(2, 1);
        tracer.go(2, -1);
        tracer.go(1, -2);
        tracer.go(-1, -2);
        tracer.go(-2, 1);
        tracer.go(-2, -1);
    }
}
