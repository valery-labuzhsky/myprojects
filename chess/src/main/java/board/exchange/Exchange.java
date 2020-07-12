package board.exchange;

import board.Logged;
import board.Square;
import board.pieces.Piece;
import board.situation.Analytics;
import board.situation.PieceScore;
import board.situation.ScoreWatcher;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange implements Logged, Analytics {
    // TODO use one exchange state for both calculator, result

    public final Square square;
    public Piece piece;
    public final HashMap<Integer, Side> sides = new HashMap<>();
    final HashMap<Piece, Integer> costs = new HashMap<>();
    public int color;
    int score;
    private Result result;

    public Exchange(Square square, int color) {
        this.square = square;
        this.piece = square.piece;
        this.color = color;
        Stream.of(-1, 1).forEach(s -> sides.put(s, new Side()));
        setScene();
    }

    // TODO it creates effective copy of ComplexExchange but without ComplexExchange itself
    //  I gonna change it
    private Exchange(Exchange exchange) {
        this.square = exchange.square;
        this.piece = exchange.piece;
        this.color = exchange.color;
        Stream.of(-1, 1).forEach(s -> sides.put(s, new Side(exchange.sides.get(s))));
        costs.putAll(exchange.costs);
    }

    public static ScoreWatcher diff(Piece piece) {
        return PieceScore.diff(piece, p -> new Exchange(p.square, -p.color));
    }

    private void setScene() {
        HashMap<Integer, TreeSet<LinkedList<Piece>>> lines = new HashMap<>();
        Stream.of(-1, 1).forEach(c -> lines.put(c, new TreeSet<LinkedList<Piece>>(
                Comparator.comparing(l -> l.getFirst(), Comparator.<Piece>comparingInt(p -> (cost(p) + p.cost()) * color).thenComparingInt(Object::hashCode))
        )));
        square.attackLines().forEach(s -> {
            LinkedList<Piece> line = new LinkedList<>();
            s.forEach(p -> {
                calculateCost(p);
                line.add(p);
            });
            if (!line.isEmpty()) {
                lines.get(line.getFirst().color).add(line);
            }
        });

        int c = color;
        while (!lines.get(c).isEmpty()) {
            LinkedList<Piece> line = lines.get(c).pollFirst();
            sides.get(c).pieces.add(line.poll());
            if (!line.isEmpty()) {
                lines.get(line.getFirst().color).add(line);
            }
            c = -c;
        }
    }

    protected void calculateCost(Piece piece) {
    }

    public String toString() {
        // TODO I need cost as well
        return "" + piece + ": " + sides.get(1).pieces + " vs " + sides.get(-1).pieces + " = " + getResult();
    }

    @Override
    public int getScore() {
        return getResult().score;
    }

    public Result getResult() {
        if (result == null) {
            result = new ExchangeCalculator(this).calculate();
        }
        return result;
    }

    public Exchange move(Piece piece) {
        Exchange copy = new Exchange(this);
        copy.score -= copy.piece == null ? 0 : copy.piece.cost();
        copy.sides.get(piece.color).pieces.remove(piece);
        copy.piece = piece;
        copy.color = -color;
        return copy;
    }

    public static class Result {
        public int score;
        int lastPlayer;

        HashMap<Integer, Side> sides = new HashMap<>();

        public Result(int score, int lastPlayer) {
            this.score = score;
            this.lastPlayer = lastPlayer;
        }

        public static class Side {
            final int piecesLeft;

            Side(int piecesLeft) {
                this.piecesLeft = piecesLeft;
            }

            public String toString() {
                return piecesLeft + " left";
            }
        }

        public String toString() {
            return "" + score; // + ", last " + lastPlayer + " " + sides;
        }
    }

    public static class Side {
        public final ArrayList<Piece> pieces = new ArrayList<>();

        Side() {
        }

        Side(Side side) {
            pieces.addAll(side.pieces);
        }
    }

    int cost(Piece piece) {
        return costs.getOrDefault(piece, 0);
    }

    @Override
    public Logger getLogger() {
        return square.getLogger();
    }

}
