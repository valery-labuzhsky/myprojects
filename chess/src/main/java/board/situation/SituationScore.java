package board.situation;

import board.exchange.DiffMoveScore;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class SituationScore implements Analytics {
    private final DefenceSituation situation;
    private final ArrayList<DefenceScore> best;

    public static ScoreWatcher diff(Piece piece) {
        return PieceScore.diff(piece, SituationScore::new);
    }

    private SituationScore(Piece piece) {
        this.situation = new DefenceSituation(piece);
        best = Situations.best(situation.defences, DiffMoveScore::getScore, piece.color);
    }

    @Override
    public int getScore() {
        return situation.score() + best.stream().map(DefenceScore::getScore).findAny().orElse(0);
    }

    @Override
    public String toString() {
        return situation.toString();
    }
}
