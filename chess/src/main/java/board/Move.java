package board;

import board.pieces.Piece;
import board.pieces.PieceType;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Created on 10.04.2020.
 *
 * @author ptasha
 */
public class Move implements Logged {
    public final Square from;
    public final Square to;
    public final PieceType promotion;
    public Piece capture;

    public final String note;

    public Move(Square from, Square to) {
        this(from, to, null, null);
    }

    public Move(Square from, Square to, PieceType promotion) {
        this(from, to, promotion, null);
    }

    public Move(Square from, Square to, PieceType promotion, String note) {
        this.from = from;
        this.to = to;
        this.promotion = promotion;
        this.note = note;
    }

    public int color() {
        return piece().color;
    }

    public Piece piece() {
        return this.from.piece;
    }

    @Override
    public String toString() {
        return "" + from.pair + to.pair + (promotion == null ? "" : promotion);
    }

    @Override
    public Logger log() {
        return Logged.log(from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Move move = (Move) o;
        return from.equals(move.from) &&
                to.equals(move.to) &&
                promotion == move.promotion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, promotion);
    }
}
