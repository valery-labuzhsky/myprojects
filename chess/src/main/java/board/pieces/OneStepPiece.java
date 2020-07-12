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
public abstract class OneStepPiece extends Piece {
    public OneStepPiece(PieceType type, Board board, int color) {
        super(type, board, color);
    }

    public abstract Stream<Square> moves();

    @Override
    public Stream<Square> whereToGo() {
        return moves().filter(s -> s.piece == null);
    }

    public Stream<Square> attacks() {
        return moves();
    }

    @Override
    public Stream<Piece> whomToAttack() {
        return attacks().map(s -> s.piece).filter(Objects::nonNull);
    }
}
