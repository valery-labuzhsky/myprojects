package board.exchange;

import board.pieces.Piece;

import java.util.function.Function;

/**
 * Created on 22.11.2020.
 *
 * @author unicorn
 */
public class FunctionPieceCosts extends CachedPieceCosts {
    Function<Piece, Integer> calculator;

    public FunctionPieceCosts(Function<Piece, Integer> calculator) {
        this.calculator = calculator;
    }

    @Override
    protected Integer calculate(Piece piece) {
        return calculator.apply(piece);
    }
}
