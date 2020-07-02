package board.situation;

import board.Move;
import board.Square;
import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.ArrayList;

import static board.Logged.tabs;

/**
 * Created on 30.06.2020.
 *
 * @author unicorn
 */
public abstract class Variants {
    protected final Piece piece;
    public final Square square;
    protected final Exchange exchange;
    final ArrayList<SamePiecesMoveScore> variants = new ArrayList<>();

    Variants(Piece piece, int color) {
        this.piece = piece;
        this.square = this.piece.square; // TODO should I use piece instead?
        exchange = square.scores.getExchange(-color);
    }

    void addSolution(Move move) {
        variants.add(new SamePiecesMoveScore(move));
    }

    public String toString() {
        return "" + exchange + tabs(variants);
    }

}
