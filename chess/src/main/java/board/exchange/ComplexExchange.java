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
    protected void setScene() {
        super.setScene();
        sides.values().forEach(s -> s.pieces.forEach(
                p -> costs.put(p, new RemoveCalculator(p, square).score())));
        sort();
    }

    private static class RemoveCalculator extends ScoredMoveCalculator<Remove> {
        private final Square exclude;

        public RemoveCalculator(Piece piece, Square exclude) {
            super(new Remove(piece), Exchange::diff);
            this.exclude = exclude;
        }

        @Override
        public void collectBefore() {
            myColor(move.piece.whomAttack());
            myColor(move.piece.whomBlock());
        }

        @Override
        public void pieces(Stream<Piece> stream) {
            super.pieces(stream.filter(p -> p.square != exclude));
        }
    }
}
