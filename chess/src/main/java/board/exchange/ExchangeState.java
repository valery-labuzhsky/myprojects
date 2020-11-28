package board.exchange;

import board.Logged;
import board.Square;
import board.math.Ray;
import board.pieces.Piece;
import board.situation.Analytics;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created on 23.11.2020.
 *
 * @author unicorn
 */
public abstract class ExchangeState implements Logged, Analytics {
    public final Square square;
    public final HashMap<Integer, Side> sides = new HashMap<>();
    final Function<Piece, Integer> costs;
    public Piece piece;
    public int color;
    int score;

    ExchangeState(Square square, int color, Function<Piece, Integer> costs) {
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

    public Stream<Piece> enemies(Piece piece) {
        return sides.get(-piece.color).pieces.stream().map(p -> p.piece);
    }

    public static class Side {
        public final int color;
        public final TreeSet<EPiece> pieces = new TreeSet<>();

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

    public static class EPiece implements Comparable<EPiece> {
        Ray ray;
        int d;
        Piece piece;
        final int cost;

        public EPiece(Piece piece) {
            this(piece, 0, null, 0);
        }

        public EPiece(Piece piece, int cost) {
            this(piece, cost, null, 0);
        }

        public EPiece(Piece piece, int cost, Ray ray, int d) {
            this.piece = piece;
            this.cost = cost;
            this.ray = ray;
            this.d = d;
        }

        @Override
        public int compareTo(EPiece piece) {
            int c = compareIt(piece);
            if (c == 0) return hashCode() - piece.hashCode();
            return c;
        }

        public int compareIt(EPiece piece) {
            if (ray != null && ray == piece.ray) {
                return d - piece.d;
            }
            return piece.getCost() - getCost();
        }

        private int getCost() {
            return (cost + piece.cost()) * piece.color;
        }
    }
}
