package board.exchange;

import board.Remove;
import board.Square;
import board.pieces.Piece;
import board.situation.PieceScore;
import board.situation.ScoreWatcher;

import java.util.stream.Stream;

/**
 * Created on 12.06.2020.
 *
 * @author unicorn
 */
public class ComplexExchange extends Exchange {
    public ComplexExchange(Square square, int color) {
        super(square, color);
    }

    public static ScoreWatcher diff(Piece piece) {
        return PieceScore.diff(piece, p -> new ComplexExchange(p.square, -p.color));
    }

    @Override
    protected void calculateCost(Piece piece) {
        costs.put(piece, new RemoveScore(piece, square).getScore());
    }

    private static class RemoveScore extends DiffMoveScore<Remove> {
        private final Square exclude;

        public RemoveScore(Piece piece, Square exclude) {
            super(new Remove(piece), Exchange::diff);
            this.exclude = exclude;
        }

        @Override
        public void collectBefore() {
            myColor(move.piece.whomToAttack());
            myColor(move.piece.whomBlock());
        }

        @Override
        public void pieces(Stream<Piece> stream) {
            super.pieces(stream.filter(p -> p.square != exclude));
        }
    }
}
