package board;

import board.pieces.Board;
import board.pieces.Move;
import board.pieces.Piece;

import java.util.HashSet;
import java.util.Objects;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Square {
    private final Board board;
    public final Pair pair;
    public Piece piece;

    public final HashSet<Waypoint> waypoints = new HashSet<>();
    public final HashSet<Attack> attacks = new HashSet<>();

    public Square(Board board, Pair pair) {
        this.board = board;
        this.pair = pair;
    }

    @Override
    public String toString() {
        return toString(this.piece) + lastMove() + marks();
    }

    private String marks() {
        StringBuilder go = new StringBuilder("");
        for (Waypoint waypoint : waypoints) {
            if (waypoint.getBlocks().isEmpty()) {
                go.append(toString(waypoint.piece));
            }
        }
        return go.length() > 0 ? "x " + go : "";
    }

    private char lastMove() {
        char s;
        Move lastMove;
        if (board.history.isEmpty()) {
            lastMove = null;
        } else {
            lastMove = board.history.getLast();
        }
        if (lastMove == null) {
            s = ' ';
        } else if (lastMove.from.equals(pair)) {
            s = '-';
        } else if (lastMove.to.equals(pair)) {
            s = '+';
        } else {
            s = ' ';
        }
        return s;
    }

    private String toString(Piece piece) {
        String p;
        if (piece == null) {
            p = " ";
        } else {
            p = piece.type.getLetter();
            p = piece.color > 0 ? p : p.toLowerCase();
        }
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Square square = (Square) o;
        return pair.equals(square.pair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair);
    }

    // TODO it calculates if I place some unknown piece from nowhere, of piece is already there
    public int getExchangeScore(int color) {
        return new Exchange(this).getScore(color);
    }

    public boolean captures(Piece piece) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.captures(piece)) {
                return true;
            }
        }
        return false;
    }

}
