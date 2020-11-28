package board.pieces;

import board.Board;
import board.Square;
import board.math.XY;

import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Bishop extends RayPiece {

    private static final XY.Transform[] TRANSFORMS = {XY.Transform.DIAGONAL};

    public Bishop(Board board, int color) {
        super(PieceType.Bishop, board, color);
    }

    @Override
    public boolean isMove(Square from, Square to) {
        return Math.abs(from.pair.file - to.pair.file) == Math.abs(from.pair.rank - to.pair.rank);
    }

    @Override
    protected XY.Transform[] getTransforms() {
        return TRANSFORMS;
    }

    @Override
    public Stream<Stream<Square>> rays() {
        return Stream.concat(
                Stream.of(1, -1).map(f -> square.ray(f, f)),
                Stream.of(1, -1).map(r -> square.ray(r, -r)));
    }
}
