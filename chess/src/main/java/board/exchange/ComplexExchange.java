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
public class ComplexExchange {
    public static ScoreWatcher diff(Piece piece) {
        return PieceScore.diff(piece, p -> create(p.square, -p.color));
    }

    public static Exchange create(Square square, int color) {
        return new Exchange(square, color, new RolesPieceCosts(square));
    }

    private static class RolesPieceCosts extends CachedPieceCosts {
        private final Square square;

        public RolesPieceCosts(Square square) {
            this.square = square;
        }

        @Override
        protected Integer calculate(Piece piece) {
            return piece.meaningfulRoles().filter(r -> r.getWhom() != square.piece).mapToInt(r -> r.getScore()).sum();
        }
    }
}
