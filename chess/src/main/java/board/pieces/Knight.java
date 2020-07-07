package board.pieces;

import board.Board;
import board.MovesTracer;
import board.Square;
import board.XY;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Knight extends OneStepPiece {
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
        return isMove(from.pair.file - to.pair.file, from.pair.rank - to.pair.rank);
    }

    private boolean isMove(int dx, int dy) {
        return Math.abs(dx) * Math.abs(dy) == 2;
    }

    @Override
    public Stream<Square> getPotentialAttacks(Square to) {
        XY file = split(to.pair.file - square.pair.file);
        XY rank = split(to.pair.rank - square.pair.rank);

        normalize(file, rank);
        normalize(rank, file);

        file.swap(rank);
        rank.swap();

        if (isMove(file) && isMove(rank)) {
            return Stream.of(file, rank).map(xy -> square.go(xy.x, xy.y)).filter(s -> s != null);
        } else {
            return Stream.empty();
        }
    }

    private boolean isMove(XY xy) {
        return isMove(xy.x, xy.y);
    }

    private void normalize(XY x, XY y) {
        if (x.x == 0) {
            x.x = 3 - Math.abs(y.x);
            x.y = -x.x;
        }
    }

    private XY split(int sum) {
        XY xy = new XY(sum % 2, sum / 2);
        xy.x += xy.y;
        if (Math.abs(sum) == 1) {
            xy.y -= xy.x;
            xy.x *= 2;
        }
        return xy;
    }

    public static Stream<Square> getMoves(Square square) {
        return Stream.of(2, -2).flatMap(x -> Stream.of(1, -1).flatMap(y ->
                Stream.of(square.go(x, y), square.go(y, x)).filter(Objects::nonNull)
        ));
    }

    @Override
    public Stream<Square> moves() {
        return getMoves(square);
    }
}
