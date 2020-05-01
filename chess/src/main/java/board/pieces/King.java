package board.pieces;

import board.*;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class King extends Piece {
    public boolean moved = false;

    public King(Board board, int color) {
        super(PieceType.King, board, color);
    }

    @Override
    public void move(Pair to) {
        if (!moved) {
            // TODO I also need the knowledge of it in planning machinery
            switch (to.file) {
                case 2:
                    board.getSquare(0, to.rank).piece.move(new Pair(3, to.rank));
                    break;
                case 6:
                    board.getSquare(7, to.rank).piece.move(new Pair(5, to.rank));
                    break;
            }
            moved = true;
        } else {
            int diff = to.file - square.pair.file;
            Rook rook;
            switch (diff) {
                case -2:
                    rook = (Rook) board.getSquare(5, to.rank).piece;
                    rook.move(new Pair(7, to.rank));
                    rook.moved = false;
                    moved = false;
                    break;
                case 2:
                    rook = (Rook) board.getSquare(3, to.rank).piece;
                    rook.move(new Pair(0, to.rank));
                    rook.moved = false;
                    moved = false;
                    break;
            }
        }
        super.move(to);
    }

    @Override
    public void trace(MovesTracer tracer) {
        tracer.go(0, 1);
        tracer.go(1, 1);
        tracer.go(1, 0);
        tracer.go(1, -1);
        tracer.go(0, -1);
        tracer.go(-1, -1);
        tracer.go(-1, 0);
        tracer.go(-1, 1);

        Pair pair = tracer.start.pair;
        if (!moved && pair.rank == border() && pair.file == 4) {
            {
                Piece rook = board.getSquare(pair.go(3, 0)).piece;
                if (rook instanceof Rook && !((Rook) rook).moved) {
                    tracer.go(2, 0);
                }
            }
            {
                Piece rook = board.getSquare(pair.go(-4, 0)).piece;
                if (rook instanceof Rook && !((Rook) rook).moved) {
                    tracer.go(-2, 0);
                }
            }
        }
    }

    @Override
    public boolean attacks(Waypoint waypoint) {
        if (!super.attacks(waypoint)) {
            return false;
        }
        if (!moved) {
            return waypoint.square.pair.file != 2 && waypoint.square.pair.file != 6;
        }
        return true;
    }

    @Override
    public boolean isMove(Square from, Square to) {
        return isAttack(from, to) || (isCastling(from, to) && isCastlingAllowed(from, to));
    }

    @Override
    public boolean isAttack(Square from, Square to) {
        int file = Math.abs(from.pair.file - to.pair.file);
        int rank = Math.abs(from.pair.rank - to.pair.rank);
        return file <= 1 && rank <= 1;
    }

    @Override
    public boolean goes(Waypoint waypoint) {
        if (!super.goes(waypoint)) {
            return false;
        }
        Square from = this.square;
        Square to = waypoint.square;
        if (isCastling(from, to)) {
            return isCastlingAllowed(from, to);
        }
        return true;
    }

    private boolean isCastlingAllowed(Square from, Square to) {
        return Stream.concat(Stream.of(to), from.ray(square)).noneMatch(s -> s.captures(this) || s.piece != null) && !from.captures(this);
    }

    private boolean isCastling(Square from, Square to) {
        return !moved && Math.abs(to.pair.file - from.pair.file) == 2 && from.pair.rank == to.pair.rank && from.pair.rank == border();
    }

    @Override
    public Collection<Piece> getBlocks(Square from, Square to) {
        return Collections.emptyList();
    }

    @Override
    public Stream<Square> getPotentialAttacks(Square square) {
        Stream.Builder<Square> builder = Stream.builder();
        Pair from = this.square.pair;
        Pair to = square.pair;

        for (int f = Math.max(to.file, from.file) - 1; f < Math.min(to.file, from.file) + 1; f++) {
            for (int r = Math.max(to.rank, from.rank) - 1; r < Math.min(to.rank, from.rank) + 1; r++) {
                Square s = board.getSquare(f, r);
                if (s != null && s != this.square && s != square) {
                    builder.add(s);
                }
            }
        }
        // TODO castling
        return builder.build();
    }
}
