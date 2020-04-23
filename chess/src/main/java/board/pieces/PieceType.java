package board.pieces;

import java.util.Objects;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public enum PieceType {
    Pawn(null, 100),
    Rook('R', 500),
    Knight('N', 300),
    Bishop('B', 300),
    Queen('Q', 900),
    King('K', 100000);

    public final Character c;
    public final int score;

    PieceType(Character c, int score) {
        this.c = c;
        this.score = score;
    }

    public static PieceType get(char c) {
        for (PieceType piece : values()) {
            if (Objects.equals(piece.c, c)) {
                return piece;
            }
        }
        return null;
    }

    public String getLetter() {
        String p;
        if (this == Pawn) {
            p = "P";
        } else {
            p = toString();
        }
        return p;
    }

    public String toString() {
        if (c == null) {
            return "";
        }
        return "" + c;
    }
}
