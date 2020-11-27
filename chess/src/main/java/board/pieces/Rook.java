package board.pieces;

import board.Board;
import board.Square;
import board.XY;

import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Rook extends RayPiece {
    public static final XY.Transform[] TRANSFORMS = {XY.Transform.LINEAR};
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
    public boolean isMove(Square from, Square to) {
        return from.pair.rank == to.pair.rank ||
                from.pair.file == to.pair.file;
    }

    @Override
    protected XY.Transform[] getTransforms() {
        return TRANSFORMS;
    }

    @Override
    public Stream<Stream<Square>> rays() {
        return Stream.concat(
                Stream.of(1, -1).map(f -> square.ray(f, 0)),
                Stream.of(1, -1).map(r -> square.ray(0, r)));
    }
}
