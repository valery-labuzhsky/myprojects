package board.situation;

import board.exchange.ComplexExchange;
import board.pieces.Piece;

import java.util.function.Function;

/**
 * Created on 07.07.2020.
 *
 * @author unicorn
 */
public class SimpleAttackTroubleMaker extends AttackTroubleMaker {
    public SimpleAttackTroubleMaker(Piece piece) {
        super(piece);
    }

    @Override
    protected Function<Piece, ScoreWatcher> diff() {
        return ComplexExchange::diff;
    }
}
