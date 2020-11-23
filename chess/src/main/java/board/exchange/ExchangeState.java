package board.exchange;

import board.Logged;
import board.Square;
import board.pieces.Piece;
import board.situation.Analytics;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

/**
 * Created on 23.11.2020.
 *
 * @author unicorn
 */
public abstract class ExchangeState implements Logged, Analytics {
    public final Square square;
    public final HashMap<Integer, Side> sides = new HashMap<>();
    final PieceCosts costs;
    public Piece piece;
    public int color;
    int score;

    ExchangeState(Square square, int color, PieceCosts costs) {
        this.square = square;
        this.piece = square.piece;
        this.color = color;
        Stream.of(-1, 1).forEach(s -> sides.put(s, newSide(s)));
        this.costs = costs;
    }

    ExchangeState(ExchangeState exchange) {
        this.square = exchange.square;
        this.piece = exchange.piece;
        this.color = exchange.color;
        Stream.of(-1, 1).forEach(s -> sides.put(s, newSide(exchange.sides.get(s))));
        costs = exchange.costs;
    }

    private Side newSide(int color) {
        return new Side(color);
    }

    protected Side getSide(int color) {
        return this.sides.get(color);
    }

    protected Side newSide(Side side) {
        return new Side(side);
    }

    @Override
    public Logger getLogger() {
        return square.getLogger();
    }

    @Override
    public int getScore() {
        return score;
    }

    public static class Side {
        public final int color;
        public final LinkedList<Piece> pieces = new LinkedList<>();

        Side(int color) {
            this.color = color;
        }

        Side(Side side) {
            this(side.color);
            pieces.addAll(side.pieces);
        }

        @Override
        public String toString() {
            return "" + pieces;
        }
    }
}
