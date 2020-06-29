package board.exchange;

import board.Logged;
import board.Square;
import board.pieces.Piece;
import board.situation.Analytics;
import board.situation.PieceScore;
import board.situation.ScoreWatcher;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 * Created on 15.04.2020.
 *
 * @author ptasha
 */
public class Exchange implements Logged, Analytics {
    protected final Square square;
    private final Piece piece;
    protected final HashMap<Integer, Side> sides = new HashMap<>();
    protected final HashMap<Piece, Integer> costs = new HashMap<>();
    public final int color;
    public Result result;

    public Exchange(Square square, int color) {
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
        square.attackers().forEach(p -> sides.get(p.color).pieces.add(p));
        sort();
    }

    protected void sort() {
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

            public Side(int piecesLeft) {
                this.piecesLeft = piecesLeft;
            }

            public String toString() {
                return piecesLeft + " left";
            }
        }

        public String toString() {
            return "" + score + ", last " + lastPlayer + " " + sides;
        }
    }

    protected class Side {
        protected final ArrayList<Piece> pieces = new ArrayList<>();

        private void sort() {
            pieces.sort(Comparator.comparingInt(p -> (cost(p) + p.cost()) * color));
        }
    }

    protected int cost(Piece piece) {
        return costs.getOrDefault(piece, 0);
    }

    public Logger getLogger() {
        return square.getLogger();
    }

}
