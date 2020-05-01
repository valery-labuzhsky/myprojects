package board;

import board.pieces.Piece;
import board.pieces.PieceType;

/**
 * Created on 10.04.2020.
 *
 * @author ptasha
 */
public class Move {
    public final Pair from;
    public final Pair to;
    public final PieceType promotion;
    public Piece capture;

    public final String note;

    public Move(Pair from, Pair to) {
        this(from, to, null, null);
    }

    public Move(Pair from, Pair to, PieceType promotion) {
        this(from, to, promotion, null);
    }

    public Move(Pair from, Pair to, String note) {
        this(from, to, null, note);
    }

    public Move(Pair from, Pair to, PieceType promotion, String note) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
        this.note = note;
    }

    public static Move parse(String move) {
        PieceType promotion = null;
        switch (move.length()) {
            case 4:
                break;
            case 5:
                promotion = PieceType.get(Character.toUpperCase(move.charAt(4)));
                break;
            default:
                return null;
        }
        int x1 = Pair.getFile(move.charAt(0));
        int y1 = Pair.parseRank(move.charAt(1));
        int x2 = Pair.getFile(move.charAt(2));
        int y2 = Pair.parseRank(move.charAt(3));
        if (x1 < 0 || y1 < 0 || x2 < 0 || y2 < 0) {
            return null;
        }
        return new Move(new Pair(x1, y1), new Pair(x2, y2), promotion);
    }

    @Override
    public String toString() {
        return "" + from + to + (promotion == null ? "" : promotion);
    }
}
