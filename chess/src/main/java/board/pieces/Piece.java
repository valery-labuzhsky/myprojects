package board.pieces;

import board.*;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public abstract class Piece implements Logged {
    public final Board board;
    public final int color;
    public final PieceType type;
    public Square square;

    public final HashSet<Waypoint> waypoints = new HashSet<>();

    public Piece(PieceType type, Board board, int color) {
        this.board = board;
        this.color = color;
        this.type = type;
    }

    public Move move(Square to) {
        return new Move(this.square, to);
    }

    public boolean isInDanger() {
        return this.square.captures(this);
    }

    public void makeMove(Square to) {
        // TODO it may be en passant
        square.piece = null;
        marksOff();
        put(to);
    }

    public void put(Square square) {
        this.square = square;
        this.square.piece = this;
        trace(new Waypoint.Origin(this, this.square));
    }

    public void add(Square square) {
        board.score += this.color * this.type.score;
        board.pieces.get(color).add(this);
        put(square);
    }

    public void remove() {
        this.square.piece = null;
        board.pieces.get(color).remove(this);
        marksOff();
        board.score -= color * type.score;
    }

    public abstract void trace(MovesTracer tracer);

    public void trace(Square start, Function<Square, Boolean> listener) {
        trace(new MovesTracer(board, start) {
            @Override
            protected boolean step() {
                return listener.apply(now);
            }
        });
    }

    private void marksOff() {
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

    protected int border() {
        return (7 - color * 7) / 2;
    }

    public boolean goes(Waypoint waypoint) {
        return true;
    }

    public boolean attacks(Waypoint waypoint) {
        return true;
    }

    public boolean isGo(Square from, Square to) {
        return isMove(from, to);
    }

    public boolean isAttack(Square from, Square to) {
        return isMove(from, to);
    }

    public boolean attacks(Square from, Square to) {
        return isAttack(from, to) && getBlocks(from, to).isEmpty();
    }

    public abstract boolean isMove(Square from, Square to);

    public boolean moves(Square to) {
        return moves(square, to);
    }

    public boolean moves(Square from, Square to) {
        return (to.piece == null || to.piece.color != color) && isMove(from, to) && getBlocks(from, to).isEmpty();
    }

    public Collection<Piece> getBlocks(Square from) {
        return getBlocks(from, this.square);
    }

    public Collection<Piece> getBlocks(Square from, Square to) {
        return new Blocks(() -> from.ray(to));
    }

    public abstract Stream<Square> getPotentialAttacks(Square square);

    public Stream<Square> getAttacks(Square square) {
        return getPotentialAttacks(square).
                filter(s -> moves(s)).
                filter(s -> attacks(s, square));
    }

    public Stream<Square> ray(Square from) {
        return from.ray(this.square);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Piece piece = (Piece) o;
        return square.equals(piece.square);
    }

    @Override
    public int hashCode() {
        return square.hashCode();
    }

    protected Stream<Square> getPotentialAttacks(Square square, XY.Transform... transforms) {
        XY from = new XY();
        XY to = new XY();
        return Stream.of(transforms).
                flatMap(t -> {
                    from.set(this.square.pair);
                    to.set(square.pair);
                    t.transform(from);
                    t.transform(to);
                    from.swap(to);
                    if (t.back(from) && t.back(to)) {
                        return Stream.of(board.getSquare(from.x, from.y), board.getSquare(to.x, to.y)).filter(s -> s != null);
                    }
                    return Stream.empty();
                });
    }
}
