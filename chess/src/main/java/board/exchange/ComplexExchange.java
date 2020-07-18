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

    // TODO it's better not to have some abstract remove score but have information about where it required and why
    //  I need to implement this logic in Exchange
    //  it doesn't make much sense except that removing cost because of waypoints
    //  I need doing it anyway

    // TODO I'll have some method in Piece which return it's participation
    //  I must create roles at last: A blocks B from C
    //  that would be great!
    //  it will have a score for not doing its part
    private static class RemoveScore extends DiffMoveScore<Remove> {
        private final Square exclude;

        public RemoveScore(Piece piece, Square exclude) {
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
