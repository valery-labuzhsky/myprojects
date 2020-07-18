package board.situation;

import board.Move;
import board.Square;
import board.exchange.ComplexExchange;
import board.exchange.DiffMoveScore;
import board.pieces.Piece;

/**
 * Created on 21.06.2020.
 *
 * @author unicorn
 */
public class SamePiecesMoveScore extends DiffMoveScore<Move> {

    public SamePiecesMoveScore(Move move) {
        super(move, ComplexExchange::diff);
    }

    @Override
    public void collectBefore() {
        Square to = move.to;
        if (to.piece != null) {
            score.collect(new CaptureScore(to.piece));
            myColor(to.piece.whomAttack()); // whom he attack or guard
        }

        Piece piece = move.piece;
        score.collect(diff.apply(piece));
        myColor(piece.whomAttack()); // whom I attack
        myColor(piece.whomBlock()); // whom I block
    }

    @Override
    public void collectAfter() {
        Piece piece = move.piece;
        myColor(piece.whomAttack()); // whom I will attack
        myColor(piece.whomBlock()); // whom I will block
    }

}
