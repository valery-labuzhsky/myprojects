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
        getPieceCache().add(this);
        getSquareCache().add(this);
    }

    protected HashSet<Waypoint> getSquareCache() {
        return this.square.waypoints;
    }

    protected HashSet<Waypoint> getPieceCache() {
        return this.piece.waypoints;
    }

    public Waypoint next(Waypoint next) {
        if (next != null) {
            this.next = next;
            next.prev = this;
            for (Piece obstruct : obstructed) {
                next.obstruct(obstruct);
            }
            if (square.piece != null) {
                next.obstruct(square.piece);
            }
        }
        return next;
    }

    boolean canGo() {
        return this.piece.goes(this);
    }

    public int getScore() {
        int score = this.piece.getScore(this.square);
        if (square.piece != null) {
            score += square.piece.type.score;
        }
        return score;
    }

    public void remove() {
        while (!obstructed.isEmpty()) {
            free(obstructed.iterator().next());
        }
        getPieceCache().remove(this);
        getSquareCache().remove(this);
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
            return attacks(piece);
        }
        return false;
    }

    public boolean canAttack() {
        return attacks(square.piece);
    }

    public boolean attacks(Piece piece) {
        if (this.piece.captures(this)) {
            if (obstructed.isEmpty()) {
                return true;
            } else if (obstructed.size() == 1) {
                return obstructed.contains(piece);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return piece.type.c + "" + piece.square.pair + "" + square.pair;
    }

    public static class Origin {
        final Piece piece;
        final Square square;

        public Origin(Piece piece, Square square) {
            this.piece = piece;
            this.square = square;
        }

        protected void markLine(int file, int rank) {
            Waypoint waypoint = mark(file, rank);
            while (waypoint != null) {
                waypoint = mark(waypoint, file, rank);
            }
        }

        protected Waypoint mark(Waypoint waypoint, int file, int rank) {
            return waypoint.next(mark(waypoint.square, file, rank));
        }

        protected Waypoint mark(int file, int rank) {
            return mark(this.square, file, rank);
        }

        protected Waypoint mark(Square square, int file, int rank) {
            Pair pair = square.pair.go(file, rank);
            if (pair.isValid()) {
                return create(this.piece.board.getSquare(pair));
            } else {
                return null;
            }
        }

        public Waypoint create(Square square) {
            Waypoint waypoint = new Waypoint(piece, square);
            piece.marksOn(new Attack.Origin(waypoint));
            return waypoint;
        }
    }
}
