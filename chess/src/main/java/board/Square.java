package board;

import board.exchange.Exchange;
import board.pieces.Knight;
import board.pieces.Piece;
import board.pieces.Queen;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public class Square implements Logged {
    public final Board board;
    public final Pair pair;
    public Piece piece;

    public final Scores scores = new Scores(this);

    public final HashSet<Waypoint> waypoints = new HashSet<>();

    public Square(Board board, Pair pair) {
        this.board = board;
        this.pair = pair;
    }

    public Piece findPieceOnRay(int file, int rank) {
        return ray(file, rank).map(s -> s.piece).filter(Objects::nonNull).findFirst().orElse(null);
    }

    public Stream<Square> ray(int file, int rank) {
        return Stream.iterate(go(file, rank), Objects::nonNull, s -> s.go(file, rank));
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
        Action lastMove = board.history.getLastMove();
        if (lastMove instanceof Move) {
            Move move = (Move) lastMove;
            if (move.from.equals(this)) {
                s = '-';
            } else if (move.to.equals(this)) {
                s = '+';
            } else {
                s = ' ';
            }
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
        return (scores.getResult(color).score - board.score) * color;
    }

    public Exchange.Result getExchangeResult(int color) {
        return scores.getResult(color);
    }

    public Logger log() {
        return pair.log();
    }

    public Square step(Pair step) {
        return board.getSquare(pair.go(step));
    }

    public Stream<Piece> attackers() {
        return Stream.concat(
                Queen.getRays(this).map(r -> r.filter(s -> s.piece != null).findFirst().orElse(null)).filter(Objects::nonNull),
                Knight.getMoves(this)
        ).map(s -> s.piece).filter(Objects::nonNull).filter(p -> p.isAttack(p.square, this));
    }

    public Square go(int file, int rank) {
        return board.getSquare(pair.file + file, pair.rank + rank);
    }
}
