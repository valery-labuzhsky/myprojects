package board;

import board.pieces.Move;
import board.pieces.Piece;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;

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

    private class FutureSquareExchange extends Exchange {
        private final Waypoint through;

        public FutureSquareExchange(Square square, int color, Waypoint through) {
            super(square, color);
            this.through = through;
        }

        @Override
        protected void gatherWaypoints() {
            super.gatherWaypoints();
            Attack attack = square.attacks.get(through);
            if (attack != null) {
                addWaypoint(attack);
            }
        }

        @Override
        protected HashSet<Piece> getBlocks(Waypoint waypoint) {
            HashSet<Piece> blocks = super.getBlocks(waypoint);
            blocks.remove(piece);
            waypoint = waypoint.prev; // TODO I can optimize it by changing getBlocks method - I'm going this way twice
            while (waypoint != null) {
                if (waypoint.square == through.square) {
                    blocks.add(piece);
                    break;
                }
                waypoint = waypoint.prev;
            }
            return blocks;
        }

        @Override
        public int getScore() {
            int score = super.getScore();
            log().debug("Score: " + score);
            return score;
        }

        public Logger log() {
            return LogManager.getLogger(through.log().getName() + "." + super.square.log().getName());
        }

    }

    public int getScore() {
        Function<Piece, Integer> score = p -> - -p.square.getScore(-p.color) * p.color * piece.color
                + -new FutureSquareExchange(p.square, -p.color, this).getScore() * p.color * piece.color;

        HashMap<Piece, Integer> affected = new HashMap<>();
        for (Waypoint waypoint : piece.waypoints) { // whom I attack or guard
            if (waypoint.square.piece != null && waypoint.square.piece.color == piece.color) {
                affected.computeIfAbsent(waypoint.square.piece, score);
            }
        }

        for (Waypoint waypoint : piece.square.waypoints) { // whom I block
            Piece piece = waypoint.getNearestPiece();
            if (piece != null && piece.color == this.piece.color) {
                affected.computeIfAbsent(piece, score);
            }
        }

        piece.trace(square.pair, pair -> { // whom I will attack or guard
            Piece p = piece.board.getSquare(pair).piece;
            if (p != null && p != piece && p.color == piece.color) {
                affected.computeIfAbsent(p, score);
                return false;
            }
            return true;
        });

        for (Waypoint waypoint : square.waypoints) { // whom I will block
            Piece piece = waypoint.getNearestPiece();
            if (piece != null && piece != this.piece && piece.color == this.piece.color) {
                affected.computeIfAbsent(piece, score);
            }
        }

        int s = new WaypointExchange(this).getScore();
        for (Integer value : affected.values()) {
            s += value;
        }

        log().debug(this + ": " + s + " " + affected);

        return s;
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
