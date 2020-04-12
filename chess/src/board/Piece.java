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

    public final HashSet<Waypoint> waypoints = new HashSet<>();
    public final HashSet<Waypoint> obstructs = new HashSet<>();

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

        for (Waypoint waypoint : square.waypoints) {
            Waypoint next = waypoint.next;
            if (next != null) {
                next.obstruct(this);
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

    private void markLine(Waypoint prev, int file, int rank) {
        prev = mark(prev, file, rank);
        if (prev != null) {
            markLine(prev, file, rank);
        }
    }

    protected void mark(int file, int rank) {
        mark(null, file, rank);
    }

    protected Waypoint mark(Waypoint prev, int file, int rank) {
        Square square;
        if (prev != null) {
            square = prev.square;
        } else {
            square = this.square;
        }
        Pair pair = square.pair.go(file, rank);
        if (pair.isValid()) {
            square = board.getSquare(pair);
            return new Waypoint(this, square, prev);
        } else {
            return null;
        }
    }

    private void marksOff() {
        while (!obstructs.isEmpty()) {
            obstructs.iterator().next().free(this);
        }
        while (!waypoints.isEmpty()) {
            waypoints.iterator().next().remove();
        }
    }

    public List<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (Waypoint waypoint : waypoints) {
            waypoint.enrich(moves);
        }
        return moves;
    }

    protected Move move(Waypoint waypoint) {
        if (waypoint.square.piece != null && waypoint.square.piece.color == color) {
            return null;
        }
        // TODO for every piece I must know which other piece I'm putting in danger
        // TODO combining it with the knowledge of all the pieces I'm protecting gives me knowledge whether or not the move is worth taking
        // TODO I must know all the obscured marks
        return new Move(square.pair, waypoint.square.pair);
    }

    public boolean canMove() {
        return !getMoves().isEmpty();
    }

    @Override
    public String toString() {
        return "" + type.name() + "[" + square.pair + "]";
    }

    public boolean captures(Waypoint waypoint) {
        return true;
    }
}
