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
    public final HashSet<Attack> attacks = new HashSet<>();
    public final HashSet<Waypoint> obstructs = new HashSet<>();

    public Piece(PieceType type, Board board, int color) {
        this.board = board;
        this.color = color;
        this.type = type;
    }

    public void move(Board board, Pair to) {
        Square dest = board.getSquare(to);
        // TODO it may be en passant
        // TODO Castling

        square.piece = null;
        marksOff();
        put(dest);
    }

    public void put(Square square) {
        this.square = square;
        this.square.piece = this;
        marksOn(new Waypoint.Origin(this, this.square));

        for (Waypoint waypoint : this.square.waypoints) {
            Waypoint next = waypoint.next;
            if (next != null) {
                next.obstruct(this);
            }
        }
    }

    public void add(Square square) {
        board.score += this.color * this.type.score;
        board.pieces.add(this);
        put(square);
    }

    public void remove() {
        this.square.piece = null;
        board.pieces.remove(this);
        marksOff();
        board.score -= color * type.score;
    }

    protected abstract void marksOn(Waypoint.Origin origin); // TODO now I need to go the same way but differently

    private void marksOff() {
        while (!obstructs.isEmpty()) {
            obstructs.iterator().next().free(this);
        }
        while (!attacks.isEmpty()) {
            attacks.iterator().next().remove();
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
        // TODO for every piece I must know which other piece I'm putting in danger
        // TODO combining it with the knowledge of all the pieces I'm protecting gives me knowledge whether or not the move is worth taking
        // TODO I must know all the obscured marks
        if (!goes(waypoint)) {
            return null; // TODO won't be needed if I check goes beforehand every time
        }
        return new Move(square.pair, waypoint.square.pair);
    }

    public boolean goes(Waypoint waypoint) {
        return (waypoint.square.piece == null || waypoint.square.piece.color != color) && waypoint.obstructed.isEmpty();
    }

    @Override
    public String toString() {
        return "" + type.name() + "[" + square.pair + "]";
    }

    public boolean captures(Waypoint waypoint) {
        return true;
    }

    public int getScore(Square square) {
        for (Waypoint waypoint : square.waypoints) {
            if (waypoint.captures(this)) {
                return -this.type.score;
            }
        }
        return 0;
    }
}
