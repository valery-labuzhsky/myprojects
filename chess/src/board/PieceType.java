package board;

import java.util.Objects;

/**
 * Created on 09.04.2020.
 *
 * @author ptasha
 */
public enum PieceType {
    Pawn(null), Rook('R'), Knight('N'), Bishop('B'), Queen('Q'), King('K');

    final Character c;

    PieceType(Character c) {
        this.c = c;
    }

    public static PieceType get(char c) {
        for (PieceType piece : values()) {
            if (Objects.equals(piece.c, c)) {
                return piece;
            }
        }
        return null;
    }

    public String toString() {
        if (c == null) {
            return "";
        }
        return "" + c;
    }
}
