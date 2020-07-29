package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.stream.Stream;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class AfterEscapePieceScore implements Analytics {
    private final CaptureProblemSolver situation;
    private final int score;

    private AfterEscapePieceScore(Exchange exchange) {
        CaptureProblem problem = CaptureProblem.findProblem(exchange);
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
