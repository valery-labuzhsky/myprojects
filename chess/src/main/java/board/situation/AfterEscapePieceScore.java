package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.Comparator;
import java.util.stream.Stream;

import static board.Logged.tabs;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class AfterEscapePieceScore implements Analytics {
    private final CaptureProblemSolver situation;
    private final int score;

    private AfterEscapePieceScore(Exchange exchange) {
        CaptureVariantProblem problem = CaptureVariantProblem.findProblem(exchange);
        if (problem != null) {
            this.situation = problem.solve();
            Solution best = situation.solutions.stream().max(Comparator.comparingInt(s -> s.whyNot().getScore(s.move.piece))).orElse(null);
            if (best == null) {
                score = situation.getScore();
            } else {
                score = best.whyNot().getScore();
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
        return situation != null ?
                tabs("Then", situation.solutions) :
                tabs("Then what?", Stream.empty());
    }

    public static Stream<AttackProblem> findProblems(Piece piece) {
        return evolve(SimpleAttackProblem.findProblems(piece).stream());
    }

    public static Stream<AttackProblem> evolve(Stream<SimpleAttackProblem> problems) {
        return problems.
                map(p -> evolve(p));
    }

    public static AttackProblem evolve(SimpleAttackProblem p) {
        return new AfterEscapeAttackProblem(p);
    }

    private static class AfterEscapeAttackProblem extends AttackProblem {
        public AfterEscapeAttackProblem(SimpleAttackProblem attack) {
            super(attack.piece, attack.move, new AfterEscapePieceScore(attack.then()));
        }
    }
}
