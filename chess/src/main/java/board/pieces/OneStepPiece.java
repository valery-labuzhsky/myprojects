package board.pieces;

import board.Board;
import board.Square;
import board.XY;

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

    public Stream<Square> attacks() {
        return moves();
    }

    @Override
    public Stream<Square> whereToGo() {
        return moves().filter(s -> s.piece == null);
    }

    @Override
    public Stream<Piece> whomAttack() {
        return attacks().map(s -> s.piece).filter(Objects::nonNull);
    }

    @Override
    protected Stream<Square> planPotentialBlock(Square friend, Square enemy) {
        XY from = new XY();
        XY to = new XY();
        XY.Transform t0 = XY.Transform.normal(from, friend, enemy);
        return moves().filter(s -> {
            to.set(s.pair);
            t0.transform(to);
            if (to.y != 0) return false;
            if (to.x <= 0) return false;
            if (to.x >= from.x) return false;
            return true;
        });
    }

}
