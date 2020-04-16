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

    public Square getOriginalSquare() {
        return this.piece.square;
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

    public boolean isMove() {
        if (square.piece == null) {
            return isGo();
        } else if (square.piece.color != piece.color) {
            return isAttack();
        } else {
            return false;
        }
    }

    public boolean moves() {
        return isMove() && getBlocks().isEmpty();
    }

    public boolean isGo() {
        return this.piece.goes(this);
    }

    public boolean goes() {
        return isGo() && getBlocks().isEmpty() && square.piece == null;
    }

    public boolean isAttack() {
        return this.piece.attacks(this);
    }

    public boolean attacks() {
        return isAttack() && getBlocks().isEmpty();
    }

    public boolean isCapture() {
        return isAttack() && square.piece != null && square.piece.color != piece.color;
    }

    public boolean captures() {
        return isCapture() && getBlocks().isEmpty();
    }

    public boolean captures(Piece piece) {
        if (isAttack() && this.piece.color != piece.color) {
            for (Piece block : getBlocks()) {
                if (block != piece) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean isGuard() {
        return isAttack() && square.piece != piece && square.piece != null && square.piece.color == piece.color;
    }

    public boolean guards() {
        return isGuard() && getBlocks().isEmpty();
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
                        if (point.square.piece != null) {
                            return true;
                        }
                        point = point.prev;
                    }
                    return false;
                }

                @Override
                public Piece next() {
                    try {
                        return point.square.piece;
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
