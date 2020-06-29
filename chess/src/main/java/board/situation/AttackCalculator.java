package board.situation;

import board.Move;
import board.Square;
import board.exchange.ScoredMoveCalculator;
import board.pieces.Piece;

import java.util.function.Function;

/**
 * Created on 21.06.2020.
 *
 * @author unicorn
 */
public class AttackCalculator extends ScoredMoveCalculator<Move> {

    public AttackCalculator(Move move, Function<Piece, ScoreWatcher> diff) {
        super(move, diff);
    }

    @Override
    public void collectBefore() {
        Square to = move.to;
        if (to.piece != null) {
            oppositeColor(to.piece.whomAttack()); // whom he attack or guard
        }

        Piece piece = move.piece;
        oppositeColor(piece.whomAttack()); // whom I attack
        oppositeColor(piece.whomBlock()); // whom I block
    }

    @Override
    public void collectAfter() {
        Piece piece = move.piece;
        oppositeColor(piece.whomAttack()); // whom I will attack
        oppositeColor(piece.whomBlock()); // whom I will block
    }
}
