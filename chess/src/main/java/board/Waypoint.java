package board;

import board.pieces.Move;
import board.pieces.Piece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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
        register();
    }

    protected void register() {
        this.square.waypoints.add(this);
    }

    protected void unregister() {
        this.square.waypoints.remove(this);
    }

    public Waypoint prev(Waypoint prev) {
        if (prev != null) {
            prev.next = this;
        }
        this.prev = prev;
        return this;
    }

    public Piece getNearestPiece() {
        Waypoint waypoint = this;
        while ((waypoint = waypoint.next) != null) {
            Piece piece = waypoint.square.piece;
            if (piece != null) {
                return piece;
            }
        }
        return null;
    }

    public Square getOriginalSquare() {
        return this.piece.square;
    }

    public Collection<Piece> getBlocks() {
        return blocks;
    }

    protected HashSet<Waypoint> getPieceCache() {
        return this.piece.waypoints;
    }

    public void remove() {
        getPieceCache().remove(this);
        unregister();
    }

    public Move move() {
        return new Move(this.piece.square.pair, square.pair);
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
        int waypointScore = new WaypointExchange(this).getScore();
        int squareScore = piece.square.getScore(-piece.color);

        log().debug(this + ": " + waypointScore + " + " + squareScore);

        return waypointScore + squareScore;
    }

    public Logger log() {
        return LogManager.getLogger(piece.log().getName() + "." + square.log().getName());
    }

    @Override
    public String toString() {
        return piece.type.getLetter() + "" + piece.square.pair + "" + square.pair;
    }

    public static class Origin extends MovesTracer {
        final Piece piece;
        Waypoint waypoint;

        public Origin(Piece piece, Square square) {
            super(square.pair);
            this.piece = piece;
        }

        @Override
        public void start() {
            super.start();
            waypoint = null;
        }

        @Override
        protected boolean step() {
            Square square = this.piece.board.getSquare(pair);
            waypoint = create(square).prev(waypoint);
            return true;
        }

        public Waypoint create(Square square) {
            Waypoint waypoint = new Waypoint(piece, square);
            piece.trace(new Attack.Origin(waypoint));
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
