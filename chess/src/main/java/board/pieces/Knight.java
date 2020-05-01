package board.pieces;

import board.Board;
import board.MovesTracer;
import board.Square;
import board.XY;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

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

    @Override
    public Collection<Piece> getBlocks(Square from, Square to) {
        return Collections.emptyList();
    }

    @Override
    public boolean isMove(Square from, Square to) {
        return Math.abs(from.pair.file - to.pair.file) * Math.abs(from.pair.rank - to.pair.rank) == 2;
    }

    @Override
    public Stream<Square> getPotentialAttacks(Square square) {
        XY file = split(this.square.pair.file - square.pair.file);
        XY rank = split(this.square.pair.rank - square.pair.rank);

        normalize(file, rank);
        normalize(rank, file);

        file.swap(rank);
        rank.swap();

        return Stream.of(file, rank).map(xy -> board.getSquare(xy.x, xy.y)).filter(s -> s != null);
    }

    public void normalize(XY x, XY y) {
        if (x.x == 0) {
            x.x = 3 - Math.abs(y.x);
            x.y = -x.x;
        }
    }

    public XY split(int sum) {
        XY xy = new XY(sum % 2, sum / 2);
        xy.x += xy.y;
        if (Math.abs(sum) == 1) {
            xy.y -= xy.x;
            xy.x *= 2;
        }
        return xy;
    }
}
