package board;

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

    public final HashSet<Mark> marks = new HashSet<>();

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
        for (Mark mark : marks) {
            if (mark.obstructs.isEmpty()) {
                go.append(toString(mark.piece));
            }
        }
        return go.length() > 0 ? "x " + go : "";
    }

    private char lastMove() {
        char s;
        Move lastMove = board.history.getLast();
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
            if (piece.type == PieceType.Pawn) {
                p = "P";
            } else {
                p = piece.type.toString();
            }
            p = piece.color > 0 ? p : p.toLowerCase();
        }
        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Square square = (Square) o;
        return pair.equals(square.pair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pair);
    }
}
