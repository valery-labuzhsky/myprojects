package board;

import board.pieces.Move;
import board.pieces.Piece;

import java.util.*;

/**
 * Created on 11.04.2020.
 *
 * @author ptasha
 */
public class Waypoint {
    Waypoint next;
    public Waypoint prev;
    public Piece piece;
    public Square square;
    private final Blocks blocks = new Blocks();

    public Waypoint(Piece piece, Square square) {
        this.piece = piece;
        this.square = square;
        getPieceCache().add(this);
        getSquareCache().add(this);
    }

    public Collection<Piece> getBlocks() {
        return blocks;
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
        }
        return next;
    }

    public void remove() {
        getPieceCache().remove(this);
        getSquareCache().remove(this);
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

    public boolean canGo() {
        return this.piece.goes(this);
    }

    boolean isCapture() {
        return this.piece.captures(this);
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
            for (Piece block : getBlocks()) {
                if (block != piece) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public int getScore() {
        int score = this.piece.getScore(this.square);
        if (square.piece != null) {
            score += square.piece.type.score;
        }
        return score;
    }

    @Override
    public String toString() {
        return piece.type.getLetter() + "" + piece.square.pair + "" + square.pair;
    }

    public static class Origin {
        final Piece piece;
        final Square square;

        public Origin(Piece piece, Square square) {
            this.piece = piece;
            this.square = square;
        }

        public void markLine(int file, int rank) {
            Waypoint waypoint = mark(file, rank);
            while (waypoint != null) {
                waypoint = mark(waypoint, file, rank);
            }
        }

        public Waypoint mark(Waypoint waypoint, int file, int rank) {
            return waypoint.next(mark(waypoint.square, file, rank));
        }

        public Waypoint mark(int file, int rank) {
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

    private class Blocks extends AbstractCollection<Piece> {
        @Override
        public Iterator<Piece> iterator() {
            return new Iterator<>() {
                Waypoint point = Waypoint.this.prev;

                @Override
                public boolean hasNext() {
                    while (point != null) {
                        if (point.piece != null) {
                            return true;
                        }
                        point = point.prev;
                    }
                    return false;
                }

                @Override
                public Piece next() {
                    try {
                        return point.piece;
                    } finally {
                        point = point.prev;
                    }
                }
            };
        }

        @Override
        public int size() {
            int size = 0;
            for (Iterator<Piece> iterator = this.iterator(); iterator.hasNext(); size++) {
                iterator.next();
            }
            return size;
        }
    }
}
