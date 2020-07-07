package board.situation;

import board.exchange.ComplexExchange;
import board.pieces.Piece;

import java.util.function.Function;

/**
 * Created on 07.07.2020.
 *
 * @author unicorn
 */
public class MyAttacksPieceScore extends Attacks {
    public MyAttacksPieceScore(Piece piece) {
        super(piece);
    }

    @Override
    protected Function<Piece, ScoreWatcher> diff() {
        return ComplexExchange::diff;
    }
}
