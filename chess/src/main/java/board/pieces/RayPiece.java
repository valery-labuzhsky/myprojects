package board.pieces;

import board.Board;
import board.Square;
import board.math.XY;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 02.06.2020.
 *
 * @author unicorn
 */
public abstract class RayPiece extends Piece {
    RayPiece(PieceType type, Board board, int color) {
        super(type, board, color);
    }

    @Override
    public Stream<Square> planPotentialAttacks(Square square) {
        XY from = new XY();
        XY to = new XY();
        return Stream.of(getTransforms()).
                flatMap(t -> {
                    from.set(this.square.pair);
                    to.set(square.pair);
                    t.transform(from);
                    t.transform(to);
                    from.swap(to);
                    if (t.back(from) && t.back(to)) {
                        return Stream.of(board.getSquare(from.x, from.y), board.getSquare(to.x, to.y)).filter(Objects::nonNull);
                    }
                    return Stream.empty();
                });
    }

    @Override
    protected Stream<Square> planPotentialBlock(Square friend, Square enemy) {
        XY from = new XY();
        XY to = new XY();
        return Stream.of(getTransforms()).
                flatMap(t -> {
                    from.set(friend.pair).minus(this.square.pair);
                    to.set(enemy.pair).minus(this.square.pair);
                    t.transform(from);
                    t.transform(to);

                    if (from.x == to.x) {
                        if (from.y * to.y >= 0) {
                            return Stream.empty();
                        }
                        from.y = 0;
                    } else if (from.y == to.y) {
                        if (from.x * to.x >= 0) {
                            return Stream.empty();
                        }
                        from.x = 0;
                    } else {
                        return Stream.empty();
                    }

                    if (t.back(from)) {
                        from.plus(this.square.pair);
                        return Stream.of(board.getSquare(from.x, from.y)).filter(Objects::nonNull);
                    }
                    return Stream.empty();
                });
    }

    protected abstract XY.Transform[] getTransforms();

    public abstract Stream<Stream<Square>> rays();

    @Override
    public Stream<Square> whereToGo() {
        return rays().flatMap(r -> r.takeWhile(s -> s.piece == null));
    }

    @Override
    public Stream<Piece> whomAttack() {
        return rays().map(r -> r.map(s -> s.piece).filter(Objects::nonNull).findFirst().orElse(null)).filter(Objects::nonNull);
    }
}
