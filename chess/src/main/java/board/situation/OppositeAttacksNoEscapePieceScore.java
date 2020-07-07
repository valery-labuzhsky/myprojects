package board.situation;

import board.pieces.Piece;

import java.util.function.Function;

/**
 * Created on 03.07.2020.
 *
 * @author unicorn
 */
public class OppositeAttacksNoEscapePieceScore extends Attacks {

    public OppositeAttacksNoEscapePieceScore(Piece piece) {
        super(piece);
    }

    @Override
    protected Function<Piece, ScoreWatcher> diff() {
        return AfterEscapePieceScore::diff;
    }

}
