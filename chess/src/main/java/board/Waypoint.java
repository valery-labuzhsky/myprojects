package board;

import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created on 11.04.2020.
 *
 * @author ptasha
 */
public class Waypoint implements Logged {
    Waypoint next;
    public Waypoint prev;
    public Piece piece;
    public Square square;

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

    public Square getOriginalSquare() {
        return this.piece.square;
    }

    protected HashSet<Waypoint> getPieceCache() {
        return this.piece.waypoints;
    }

    public void remove() {
        getPieceCache().remove(this);
        unregister();
    }

    public Move move() {
        return new Move(this.piece.square, square);
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

    public boolean isAttack() {
        return this.piece.attacks(this);
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

    public Collection<Piece> getBlocks() {
        return piece.getBlocks(square);
    }

    @Override
    public Logger getLogger() {
        return Logged.log(piece, square);
    }

    @Override
    public String toString() {
        return piece.type.getLetter() + "" + piece.square.pair + "" + square.pair;
    }

    public static class Origin extends MovesTracer {
        final Piece piece;
        Waypoint waypoint;

        public Origin(Piece piece, Square square) {
            super(piece.board, square);
            this.piece = piece;
        }

        @Override
        public void start() {
            super.start();
            waypoint = null;
        }

        @Override
        protected boolean step() {
            waypoint = create(now).prev(waypoint);
            return true;
        }

        public Waypoint create(Square square) {
            return new Waypoint(piece, square);
        }
    }

}
