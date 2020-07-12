package board.exchange;

import board.pieces.Piece;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created on 18.06.2020.
 *
 * @author unicorn
 */
public class ExchangeCalculator {
    private final Exchange exchange;

    private final HashMap<Integer, Side> sides = new HashMap<>();
    private int score;
    private Piece onSquare;

    public ExchangeCalculator(Exchange exchange) {
        this.onSquare = exchange.piece;
        exchange.sides.forEach((c, s) -> this.sides.put(c, new Side(c, s)));
        this.exchange = exchange;
    }

    private int getScore(int color) {
        return this.score * color;
    }

    public Exchange.Result calculate() {
        return this.sides.get(exchange.color).play();
    }

    protected class Side {
        final int color;

        private final LinkedList<Piece> pieces;

        Exchange.Result bestResult;

        public Side(int color, Exchange.Side side) {
            this.color = color;
            this.pieces = new LinkedList<>(side.pieces);
        }

        private Exchange.Result getResult() {
            Exchange.Result result = new Exchange.Result(score + exchange.score, color);
            storeResults(result);
            sides.get(-color).storeResults(result);
            return result;
        }

        private void storeResults(Exchange.Result result) {
            result.sides.put(color, new Exchange.Result.Side(pieces.size()));
        }

        private Exchange.Result play() {
            int lastScore = getScore(color);

            int bestScore;
            if (bestResult == null) {
                bestScore = lastScore;
                bestResult = getResult();
            } else {
                bestScore = bestResult.score * color;
                if (bestScore < lastScore) {
                    bestResult = getResult();
                }
            }

            Exchange.Result result;

            if (pieces.isEmpty()) {
                result = bestResult;
            } else {
                Piece piece = pieces.pollFirst();
                score = score + exchange.cost(piece) - onSquare.cost();
                onSquare = piece;

                exchange.log().debug("Moving " + piece + ": " + score);

                if (getScore(color) <= bestScore) {
                    result = bestResult;
                } else {
                    result = sides.get(-color).play();
                }
            }

            return result;
        }

    }
}
