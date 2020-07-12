package board.pieces;

import board.*;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Pawn extends OneStepPiece {
    public Pawn(Board board, int color) {
        super(PieceType.Pawn, board, color);
    }

    @Override
    public void trace(MovesTracer tracer) {
        tracer.start();
        tracer.step(0, color);
        // enpassant
        int border = border();
        int secondRow = border + color;
        if (square.pair.rank == secondRow) {
            tracer.step(0, color);
        }
        tracer.end();

        tracer.go(-1, color);
        tracer.go(1, color);
    }

    @Override
    public boolean goes(Waypoint waypoint) {
        Square square = waypoint.getOriginalSquare();
        return waypoint.square.pair.file == square.pair.file;
    }

    @Override
    public boolean attacks(Waypoint waypoint) {
        Square from = waypoint.getOriginalSquare();
        return waypoint.square.pair.file != from.pair.file;
    }

    @Override
    public boolean isGo(Square from, Square to) {
        if (from.pair.file != to.pair.file) {
            return false;
        }
        switch ((to.pair.rank - from.pair.rank) * color) {
            case 1:
                return true;
            case 2:
                return from.pair.rank == border() + color;
        }
        return false;
    }

    @Override
    public boolean isAttack(Square from, Square to) {
        return from.pair.rank + color == to.pair.rank &&
                Math.abs(from.pair.file - to.pair.file) == 1;
    }

    @Override
    public boolean isMove(Square from, Square to) {
        return isGo(from, to) || isAttack(from, to);
    }

    @Override
    public boolean moves(Square from, Square to) {
        return attacks(from, to) || goes(from, to);
    }

    public boolean goes(Square from, Square to) {
        return to.piece == null && isGo(from, to) && getBlocks(from, to).isEmpty();
    }

    @Override
    public boolean attacks(Square from, Square to) {
        return isAttack(from, to);
    }

    @Override
    public Stream<Square> getPotentialAttacks(Square square) {
        Pair from = this.square.pair;
        Pair to = square.pair;
        if (Math.abs(from.file - to.file) != 1) {
            return Stream.empty();
        }
        switch ((to.rank - from.rank) * color) {
            case 2:
                return Stream.of(board.getSquare(from.go(0, color)));
            case 3:
                if (from.rank == border() + color) {
                    return Stream.of(board.getSquare(from.go(0, 2 * color)));
                }
        }
        return Stream.empty();
    }

    @Override
    public Stream<Square> moves() {
        return Stream.concat(attacks(), goes());
    }

    @Override
    public Stream<Square> attacks() {
        return Stream.of(-1, 1).map(f -> square.go(f, color)).filter(Objects::nonNull);
    }

    // TODO = whereToGo?
    public Stream<Square> goes() {
        Stream<Square> go = Stream.of(square.go(0, color));
        if (border() + color == square.pair.rank) {
            return Stream.concat(go, Stream.of(square.go(0, 2 * color)));
        }
        return go.filter(Objects::nonNull);
    }
}
