package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.Comparator;
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
        // TODO here I calculate my score, I must take new info into account
        //  but firstly I need to print it out
        if (problem != null) {
            this.situation = problem.solve();
            // TODO here it goes
            //  it must be lessened
            //  not every solution is complete one
            //  I must do it here
            //  now it must go to tempos
            // TODO I thought how can I represent these new troubles
            Solution best = situation.solutions.stream().max(Comparator.comparingInt(s -> s.whyNots() * s.move.piece.color)).orElse(null);
            if (best == null) {
                score = situation.getScore();
            } else {
                score = best.whyNots();
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
