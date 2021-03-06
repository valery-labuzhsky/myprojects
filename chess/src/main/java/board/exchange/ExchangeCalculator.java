package board.exchange;

/**
 * Created on 18.06.2020.
 *
 * @author unicorn
 */
public class ExchangeCalculator extends ExchangeResult {

    ExchangeCalculator(Exchange exchange) {
        super(exchange);
        score = exchange.score;
    }

    @Override
    protected Side newSide(Exchange.Side side) {
        return new Side(side);
    }

    @Override
    protected Side getSide(int color) {
        return (Side) super.getSide(color);
    }

    private int getScore(int color) {
        return this.getScore() * color;
    }

    ExchangeResult calculate() {
        return getSide(color).play();
    }

    protected class Side extends ExchangeResult.Side {
        ExchangeResult bestResult;

        Side(Exchange.Side side) {
            super(side);
        }

        private ExchangeResult getResult() {
            ExchangeResult result = new ExchangeResult(ExchangeCalculator.this);
            result.score = getScore();
            return result;
        }

        private ExchangeResult play() {
            int lastScore = getScore(this.color);

            int bestScore;
            if (bestResult == null) {
                bestScore = lastScore;
                bestResult = getResult();
            } else {
                bestScore = bestResult.getScore() * this.color;
                if (bestScore < lastScore) {
                    bestResult = getResult();
                }
            }

            ExchangeResult result;

            if (this.pieces.isEmpty()) {
                result = bestResult;
            } else {
                EPiece next = pieces.pollFirst();

                // TODO use move function
                score = getScore() + next.cost - piece.cost();
                getSide(-color).lost.add(piece);
                piece = next.piece;

                log().debug("Moving " + next.piece + ": " + getScore() + " best " + bestScore + ": " + bestResult);

                if (getScore(this.color) <= bestScore) {
                    result = bestResult;
                } else {
                    result = getSide(-this.color).play();
                }
            }

            return result;
        }

    }
}
