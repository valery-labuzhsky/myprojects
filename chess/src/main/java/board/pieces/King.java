package board.pieces;

import board.Board;
import board.Square;
import board.math.Pair;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class King extends OneStepPiece {
    public boolean moved = false;

    public King(Board board, int color) {
        super(PieceType.King, board, color);
    }

    @Override
    public void makeMove(Square to) {
        if (!moved) {
            // TODO I also need the knowledge of it in planning machinery
            Piece rook;
            switch (to.pair.file) {
                case 2:
                    rook = board.getSquare(0, to.pair.rank).piece;
                    rook.makeMove(board.getSquare(3, to.pair.rank));
                    break;
                case 6:
                    rook = board.getSquare(7, to.pair.rank).piece;
                    rook.makeMove(board.getSquare(5, to.pair.rank));
                    break;
            }
            moved = true;
        } else {
            int diff = to.pair.file - square.pair.file;
            Rook rook;
            switch (diff) {
                case -2:
                    rook = (Rook) board.getSquare(5, to.pair.rank).piece;
                    rook.makeMove(board.getSquare(7, to.pair.rank));
                    rook.moved = false;
                    moved = false;
                    break;
                case 2:
                    rook = (Rook) board.getSquare(3, to.pair.rank).piece;
                    rook.makeMove(board.getSquare(0, to.pair.rank));
                    rook.moved = false;
                    moved = false;
                    break;
            }
        }
        super.makeMove(to);
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

    private boolean isCastlingAllowed(Square from, Square to) {
        return Stream.concat(Stream.of(to), from.ray(square)).noneMatch(s -> s.captures(this) || s.piece != null) && !from.captures(this);
    }

    private boolean isCastling(Square from, Square to) {
        return !moved && Math.abs(to.pair.file - from.pair.file) == 2 && from.pair.rank == to.pair.rank && from.pair.rank == border();
    }

    @Override
    public Stream<Piece> getBlocks(Square from, Square to) {
        return Stream.empty();
    }

    @Override
    public Stream<Square> planPotentialAttacks(Square square) {
        Stream.Builder<Square> builder = Stream.builder();
        Pair from = this.square.pair;
        Pair to = square.pair;

        for (int f = Math.min(to.file, from.file) - 1; f < Math.max(to.file, from.file) + 1; f++) {
            for (int r = Math.min(to.rank, from.rank) - 1; r < Math.max(to.rank, from.rank) + 1; r++) {
                Square s = board.getSquare(f, r);
                if (s != null && s != this.square && s != square) {
                    builder.add(s);
                }
            }
        }
        // TODO castling
        return builder.build();
    }

    @Override
    public Stream<Square> moves() {
        return Stream.of(1, 0, -1).
                flatMap(f -> Stream.of(1, 0, -1).
                        filter(r -> f != 0 || r != 0).
                        map(r -> square.go(f, r))).
                filter(Objects::nonNull);
    }
}
