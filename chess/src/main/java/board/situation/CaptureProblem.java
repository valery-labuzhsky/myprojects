package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.stream.Stream;

/**
 * Created on 25.11.2020.
 *
 * @author unicorn
 */
public class CaptureProblem extends Problem {
    Exchange exchange;

    public CaptureProblem(Piece piece, Exchange exchange) {
        super(piece);
        this.exchange = exchange;
    }

    Stream<CaptureVariantProblem> getVariants() {
        return exchange.sides.get(-piece.color).pieces.stream().
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
