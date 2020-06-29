package board.situation;

import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class SituationScore implements Analytics {
    private final Situation situation;
    private final ArrayList<Solution> best;

    public static ScoreWatcher diff(Piece piece) {
        return PieceScore.diff(piece, SituationScore::new);
    }

    public SituationScore(Piece piece) {
        this.situation = new Situation(piece, piece.color);
        best = Solution.best(situation.solutions, Solution::getDefence, piece.color);
    }

    @Override
    public int getScore() {
        return situation.score() + best.stream().map(Solution::getDefence).findAny().orElse(0);
    }

    @Override
    public String toString() {
        return situation.toString();
    }
}
