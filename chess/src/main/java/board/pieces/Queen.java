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
public class Queen extends RayPiece {

    private static final XY.Transform[] TRANSFORMS = {
            XY.Transform.LINEAR,
            XY.Transform.DIAGONAL,
            XY.Transform.ALMOND_X_PLUS,
            XY.Transform.ALMOND_X_MINUS,
            XY.Transform.ALMOND_Y_PLUS,
            XY.Transform.ALMOND_Y_MINUS};

    public Queen(Board board, int color) {
        super(PieceType.Queen, board, color);
    }

    @Override
    public boolean isMove(Square from, Square to) {
        int file = Math.abs(from.pair.file - to.pair.file);
        int rank = Math.abs(from.pair.rank - to.pair.rank);
        return file == 0 || rank == 0 || file == rank;
    }

    @Override
    protected XY.Transform[] getTransforms() {
        return TRANSFORMS;
    }

    public static Stream<Stream<Square>> getRays(Square square) {
        return Stream.of(1, 0, -1).
                flatMap(f -> Stream.of(1, 0, -1).
                        filter(r -> f != 0 || r != 0).
                        map(r -> square.ray(f, r)));
    }

    @Override
    public Stream<Stream<Square>> rays() {
        return getRays(square);
    }
}
