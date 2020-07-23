package board.exchange;

import board.Square;
import board.pieces.Piece;
import board.situation.PieceScore;
import board.situation.ScoreWatcher;

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
        costs.put(piece, piece.meaningfulRoles().filter(r -> r.getWhom() != this.piece).mapToInt(r -> r.getScore()).sum());
    }
}
