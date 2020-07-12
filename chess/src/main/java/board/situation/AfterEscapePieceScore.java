package board.situation;

import board.exchange.DiffMoveScore;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class AfterEscapePieceScore implements Analytics {
    private final CaptureProblemSolver situation;
    private final int score;

    public static ScoreWatcher diff(Piece piece) {
        return PieceScore.diff(piece, AfterEscapePieceScore::new);
    }

    private AfterEscapePieceScore(Piece piece) {
        // TODO I need to implement part of Solutions here
        //  I need get all solutions including attacks
        //  combine them to tempos and choose the best move
        //  and take worst left

        // TODO it's very interesting recursive problem, but I need workaround for awhile
        //  I need sort out other changes first
        CaptureProblem problem = CaptureProblemSolver.createProblem(piece.getExchange());
        ArrayList<SamePiecesMoveScore> best;
        if (problem != null) {
            this.situation = problem.solve();
            best = Situations.best(situation.variants, DiffMoveScore::getScore, piece.color);
            score = situation.getScore() + best.stream().map(SamePiecesMoveScore::getScore).findAny().orElse(0);
        } else {
            situation = null;
            score = 0;
        }
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return "After escape: " + (situation == null ? "No problem" : situation.toString());
    }
}
