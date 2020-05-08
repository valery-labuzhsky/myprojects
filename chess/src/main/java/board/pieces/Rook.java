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
public class Rook extends Piece {
    public boolean moved = false;

    public Rook(Board board, int color) {
        super(PieceType.Rook, board, color);
    }

    @Override
    public void makeMove(Square to) {
        super.makeMove(to);
        moved = true;
    }

    @Override
    public void trace(MovesTracer tracer) {
        tracer.markLine(0, 1);
        tracer.markLine(1, 0);
        tracer.markLine(0, -1);
        tracer.markLine(-1, 0);
    }

    @Override
    public boolean isMove(Square from, Square to) {
        return from.pair.rank == to.pair.rank ||
                from.pair.file == to.pair.file;
    }

    @Override
    public Stream<Square> getPotentialAttacks(Square square) {
        return getPotentialAttacks(square, XY.Transform.LINEAR);
    }
}
