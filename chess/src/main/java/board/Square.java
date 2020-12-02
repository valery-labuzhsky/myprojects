package board;

import board.math.Pair;
import board.math.Ray;
import board.pieces.Knight;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

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

    @Override
    public String toString() {
        return (piece == null ? "" : piece.getLetter()) + pair;
    }

    String toBoard() {
        return (piece == null ? "." : piece.getLetter()) + lastMove();
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
        for (Piece enemy : piece.enemiesList()) {
            if (enemy.canAttack(enemy.square, this)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Logger getLogger() {
        return pair.getLogger();
    }

    private Square step(Pair step) {
        return board.getSquare(pair.go(step));
    }

    public Stream<Piece> attackers() {
        return attackLines().map(s -> s.findFirst().orElse(null)).filter(Objects::nonNull);
    }

    public Stream<Stream<Piece>> attackLines() {
        return Stream.concat(
                Ray.rays(this), Knight.getMoves(this).map(Stream::of)).
                map(r -> r.map(s -> s.piece).filter(Objects::nonNull).takeWhile(p -> p.isAttack(p.square, this)));
    }

    public Square go(int file, int rank) {
        return board.getSquare(pair.file + file, pair.rank + rank);
    }
}
