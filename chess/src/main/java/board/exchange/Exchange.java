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
    protected final Square square;
    private final Piece piece;
    final HashMap<Integer, Side> sides = new HashMap<>();
    final HashMap<Piece, Integer> costs = new HashMap<>();
    public final int color;
    public Result result;

    Exchange(Square square, int color) {
        this.square = square;
        this.piece = square.piece;
        this.color = color;
        Stream.of(-1, 1).forEach(s -> sides.put(s, new Side()));
        setScene();
        result = new ExchangeCalculator(this).calculate();
    }

    public static ScoreWatcher diff(Piece piece) {
        return PieceScore.diff(piece, p -> new Exchange(p.square, -p.color));
    }

    protected void setScene() {
        // TODO I need adding batteries back again
        //  very careful analysis of whether or not participation is required
        //  let's consider cases:
        //  1. all the battery is of the same color - I need only maintain order
        //  2. I'm hiding enemy's piece - it can be only higher piece - why it just didn't capture me?
        //  ---
        //  let's order it here and use them as usual
        //  I'll be able to make decision here as well if I need to

        // TODO I need extract these collections be able to sort them
        //  what to do with it?
        //  sort them on the fly or sort them here
        //  I need putting them to special construct anyway
        //  if I sort them right away, I'll be able to log them without second sought


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

//        square.attackers().forEach(p -> sides.get(p.color).pieces.add(p));
//        sort();
    }

    protected void calculateCost(Piece piece) {
    }

    void sort() {
        sides.values().forEach(Side::sort);
    }

    public String toString() {
        // TODO I need cost as well
        return "" + piece + ": " + sides.get(1).pieces + " vs " + sides.get(-1).pieces + " => " + result;
    }

    @Override
    public int getScore() {
        return result.score;
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

    protected class Side {
        protected final ArrayList<Piece> pieces = new ArrayList<>();

        private void sort() {
            pieces.sort(Comparator.comparingInt(p -> (cost(p) + p.cost()) * color));
        }
    }

    int cost(Piece piece) {
        return costs.getOrDefault(piece, 0);
    }

    public Logger getLogger() {
        return square.getLogger();
    }

}
