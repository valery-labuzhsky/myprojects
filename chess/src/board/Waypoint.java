package board;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 11.04.2020.
 *
 * @author ptasha
 */
public class Waypoint {
    Waypoint next;
    Waypoint prev;
    Piece piece;
    Square square;
    Set<Piece> obstructed = new HashSet<>();

    public Waypoint(Piece piece, Square square) {
        this.piece = piece;
        this.square = square;
        piece.waypoints.add(this);
        square.waypoints.add(this);
    }

    public Waypoint(Piece piece, Square square, Waypoint prev) {
        this(piece, square);
        if (prev != null) {
            prev.next = this;
            this.prev = prev;
            for (Piece obstruct : prev.obstructed) {
                obstruct(obstruct);
            }
            if (prev.square.piece != null) {
                obstruct(prev.square.piece);
            }
        }
    }

    public void remove() {
        while (!obstructed.isEmpty()) {
            free(obstructed.iterator().next());
        }
        piece.waypoints.remove(this);
        square.waypoints.remove(this);
    }

    public void obstruct(Piece piece) {
        this.obstructed.add(piece);
        piece.obstructs.add(this);
        if (next != null) {
            next.obstruct(piece);
        }
    }

    public void free(Piece piece) {
        this.obstructed.remove(piece);
        piece.obstructs.remove(this);
        if (next != null) {
            next.free(piece);
        }
    }

    public void enrich(List<Move> moves) {
        Move move = move();
        if (move != null) {
            moves.add(move);
        }
    }

    public Move move() {
        return this.piece.move(this);
    }

    public boolean captures(Piece piece) {
        if (this.piece.color != piece.color) {
            if (this.piece.captures(this)) {
                if (obstructed.isEmpty()) {
                    return true;
                } else if (obstructed.size() == 1) {
                    return obstructed.contains(piece);
                }
            }
        }
        return false;
    }
}
