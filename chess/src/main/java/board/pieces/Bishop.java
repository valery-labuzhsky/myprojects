package board.pieces;

import board.Board;
import board.MovesTracer;
import board.Square;
import board.XY;

import java.util.stream.Stream;

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

    @Override
    public boolean isMove(Square from, Square to) {
        return Math.abs(from.pair.file - to.pair.file) == Math.abs(from.pair.rank - to.pair.rank);
    }

    @Override
    public Stream<Square> getPotentialAttacks(Square square) {
        return getPotentialAttacks(square,
                XY.Transform.DIAGONAL);
    }

}
