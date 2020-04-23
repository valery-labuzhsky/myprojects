package board.pieces;

import board.*;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public abstract class Piece {
    public final Board board;
    public final int color;
    public final PieceType type;
    public Square square;

    public final HashSet<Waypoint> waypoints = new HashSet<>();
    public final HashSet<Attack> attacks = new HashSet<>();

    public Piece(PieceType type, Board board, int color) {
        this.board = board;
        this.color = color;
        this.type = type;
    }

    public void move(Pair to) {
        Square dest = board.getSquare(to);
        // TODO it may be en passant

        square.piece = null;
        marksOff();
        put(dest);
    }

    public void put(Square square) {
        this.square = square;
        this.square.piece = this;
        trace(new Waypoint.Origin(this, this.square));
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

    public abstract void trace(MovesTracer tracer);

    public void trace(Pair start, Function<Pair, Boolean> listener) {
        trace(new MovesTracer(start) {
            @Override
            protected boolean step() {
                return listener.apply(pair);
            }
        });
    }

    private void marksOff() {
        while (!attacks.isEmpty()) {
            attacks.iterator().next().remove();
        }
        while (!waypoints.isEmpty()) {
            waypoints.iterator().next().remove();
        }
    }

    public List<Waypoint> getMoves() {
        ArrayList<Waypoint> moves = new ArrayList<>();
        for (Waypoint waypoint : waypoints) {
            if (waypoint.moves()) {
                moves.add(waypoint);
            }
        }
        return moves;
    }

    public boolean goes(Waypoint waypoint) {
        return true;
    }

    public boolean attacks(Waypoint waypoint) {
        return true;
    }

    public int getScore() {
        return -square.getScore(-color);
    }

    @Override
    public String toString() {
        return "" + type.getLetter() + square.pair;
    }

    public Logger log() {
        return square.log();
    }
}
