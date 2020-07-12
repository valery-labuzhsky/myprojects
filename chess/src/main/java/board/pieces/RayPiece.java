package board.pieces;

import board.Board;
import board.Square;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 02.06.2020.
 *
 * @author unicorn
 */
public abstract class RayPiece extends Piece {
    public RayPiece(PieceType type, Board board, int color) {
        super(type, board, color);
    }

    public abstract Stream<Stream<Square>> rays();

    @Override
    public Stream<Square> whereToGo() {
        return rays().flatMap(r -> r.takeWhile(s -> s.piece == null));
    }

    @Override
    public Stream<Piece> whomToAttack() {
        return rays().map(r -> r.map(s -> s.piece).filter(Objects::nonNull).findFirst().orElse(null)).filter(Objects::nonNull);
    }
}
