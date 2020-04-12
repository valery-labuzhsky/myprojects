package board;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public abstract class Piece {
    protected final Board board;
    public final int color;
    public final PieceType type;
    public Square square;

    public final HashSet<Mark> marks = new HashSet<>();

    public Piece(PieceType type, Board board, int color) {
        this.board = board;
        this.color = color;
        this.type = type;
    }

    public void move(Board board, Pair to) throws IllegalMoveException {
        Square dest = board.getSquare(to);
        if (dest.piece != null) {
            if (dest.piece.color == color) {
                throw new IllegalMoveException("own color");
            }
            dest.piece.remove();
        }
        // TODO it may be en passant
        // TODO Castling

        square.piece = null;
        marksOff();
        square = dest;
        square.piece = this;
        marksOn();

        for (Mark mark : square.marks) {
            Mark next = mark.next;
            if (next != null) {
                next.obstructs.add(this);
                this.marks.add(next);
            }
        }
    }

    public void remove() {
        this.square.piece = null;
        board.pieces.remove(this);
        marksOff();
    }

    protected abstract void marksOn();

    protected void markLine(int file, int rank) {
        markLine(null, file, rank);
    }

    private void markLine(Mark prev, int file, int rank) {
        prev = mark(prev, file, rank);
        if (prev != null) {
            markLine(prev, file, rank);
        }
    }

    protected void mark(int file, int rank) {
        mark(null, file, rank);
    }

    protected Mark mark(Mark prev, int file, int rank) {
        Square square;
        if (prev != null) {
            square = prev.square;
        } else {
            square = this.square;
        }
        Pair pair = square.pair.go(file, rank);
        if (pair.isValid()) {
            square = board.getSquare(pair);
            return new Mark(this, square, prev);
        } else {
            return null;
        }
    }

    private void marksOff() {
        for (Mark mark : marks) {
            if (mark.piece == this) {
                mark.square.marks.remove(mark);
            } else {
                mark.obstructs.remove(this);
            }
        }
        marks.clear();
    }

    public List<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (Mark mark : marks) {
            if (mark.piece == this && mark.obstructs.isEmpty()) {
                Move move = move(mark);
                if (move != null) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    protected Move move(Mark mark) {
        if (mark.square.piece != null && mark.square.piece.color == color) {
            return null;
        }
        return new Move(square.pair, mark.square.pair);
    }

    public boolean canMove() {
        return !getMoves().isEmpty();
    }

    @Override
    public String toString() {
        return "" + type.name() + "[" + square.pair + "]";
    }

    public boolean captures(Mark mark) {
        return true;
    }
}
