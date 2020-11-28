package board.situation;

import board.exchange.Exchange;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created on 25.11.2020.
 *
 * @author unicorn
 */
public class CaptureProblem extends Problem {
    Exchange exchange;

    public CaptureProblem(Exchange exchange) {
        super(exchange.piece);
        this.exchange = exchange;
    }

    public static Optional<CaptureProblem> findProblem(Exchange exchange) {
        return Optional.of(exchange).filter(e -> e.getScore(e.piece) < 0).map(e -> new CaptureProblem(e));
    }

    Stream<CaptureVariantProblem> getVariants() {
        return exchange.enemies(piece).
                map(enemy -> exchange.move(enemy)).
                filter(problem -> problem.getScore(piece) < 0).
                map(after -> new CaptureVariantProblem(piece, this, after));
    }

    @Override
    public ProblemSolver solve() {
        return new CaptureProblemSolver(this);
    }

    @Override
    public int getScore() {
        return exchange.getScore();
    }
}
