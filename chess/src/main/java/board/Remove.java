package board;

import board.pieces.Piece;

import java.util.Objects;

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
        piece.put(piece.square);
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
}
