package board;

import board.exchange.Exchange;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Square {
    public final Board board;
    public final Pair pair;
    public Piece piece;

    public final HashSet<Waypoint> waypoints = new HashSet<>();
    public final HashMap<Waypoint, Attack> attacks = new HashMap<>();

    public Square(Board board, Pair pair) {
        this.board = board;
        this.pair = pair;
    }

    public Square diagonal(int fx, int ty) {
        return this.board.diagonal(fx, ty);
    }

    public Stream<Square> ray(Square to) {
        Pair step = this.pair.step(to.pair);
        return Stream.iterate(this, s -> s != null && s != to, s -> s.step(step)).skip(1);
    }

    public Stream<Square> line(Square to) {
        if (pair.file == to.pair.file) {
            return Stream.iterate(0, r -> r < 8, r -> ++r).map(r -> board.getSquare(pair.file, r));
        } else {
            int df = to.pair.file - pair.file;
            int dr = to.pair.rank - pair.rank;
            if (dr % df == 0) {
                dr /= df;
                df = 1;

                int fdr = dr;
                return Stream.concat(
                        Stream.iterate(this,
                                s -> s != null,
                                s -> board.getSquare(s.pair.file + 1, s.pair.rank + fdr)),
                        Stream.iterate(this,
                                s -> s != null,
                                s -> board.getSquare(s.pair.file - 1, s.pair.rank - fdr)).skip(1)
                );
            }
        }
        return Stream.empty();
    }

    @Override
    public String toString() {
        return toString(this.piece) + pair;
    }

    public String toBoard() {
        return toString(this.piece) + lastMove();
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
            p = ".";
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
        return pair.hashCode();
    }

    public boolean captures(Piece piece) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.captures(piece)) {
                return true;
            }
        }
        return false;
    }

    public int getScore(int color) {
        return new Exchange(this, color).getScore();
    }

    public Exchange.Result getExchangeResult(int color) {
        return new Exchange(this, color).getResult();
    }

    public Logger log() {
        return pair.log();
    }

    public Square step(Pair step) {
        return board.getSquare(pair.go(step));
    }
}
