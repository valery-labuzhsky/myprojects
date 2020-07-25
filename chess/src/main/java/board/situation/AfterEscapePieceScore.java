package board.situation;

import board.pieces.Piece;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class AfterEscapePieceScore implements Analytics {
    private final CaptureProblemSolver situation;
    private final int score;

    public static ScoreDiff diff(Piece piece) {
        return PieceScore.diff(piece, AfterEscapePieceScore::new);
    }

    // TODO I need splitting it
    //  I need to pass problem here
    //  diff before must be 0 only after must be counter
    private AfterEscapePieceScore(Piece piece) {
        // TODO I need to implement part of Solutions here
        //  I need get all solutions including attacks
        //  combine them to tempos and choose the best move
        //  and take worst left

        // TODO it's very interesting recursive problem, but I need workaround for a while
        //  I need sort out other changes first
        CaptureProblem problem = CaptureProblemSolver.createProblem(piece.getExchange());
        if (problem != null) {
            this.situation = problem.solve();
            if (situation.solutions.isEmpty()) {
                score = situation.getScore();
            } else {
                score = 0;
            }
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

    public static Stream<AttackProblem> findProblems(Piece piece) {
        return evolve(SimpleAttackTroubleMaker.findProblems(piece).stream());
    }

    public static Stream<AttackProblem> evolve(Stream<AttackProblem> problems) {
        return evolve(problems, p -> diff(p));
    }

    public static Stream<AttackProblem> evolve(Stream<AttackProblem> problems, Function<Piece, ScoreDiff> diff) {
        return problems.
                map(p -> new AttackProblem(p.piece, p.move, diff.apply(p.piece))).
                filter(p -> p.getScore() * p.piece.color > 0);
    }

}
