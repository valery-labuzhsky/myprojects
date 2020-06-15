package board;

import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created on 11.06.2020.
 *
 * @author unicorn
 */
public class Remove extends Action {
    public final Piece piece;

    public Remove(Piece piece) {
        this.piece = piece;
    }

    @Override
    protected Board board() {
        return piece.board;
    }

    @Override
    protected void makeMove() {
        piece.remove();
    }

    @Override
    protected void undoMove() {
        piece.add(piece.square);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Remove remove = (Remove) o;
        return piece.equals(remove.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece);
    }

    @Override
    public Logger getLogger() {
        return Logged.log(Stream.of(piece.getLogger().getName(), "-"));
    }
}
