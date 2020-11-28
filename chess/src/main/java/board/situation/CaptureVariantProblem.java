package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.stream.Stream;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public class CaptureVariantProblem extends MoveProblem {
    private final Exchange exchange;
    CaptureProblem before;

    CaptureVariantProblem(Piece piece, CaptureProblem before, Exchange exchange) {
        super(piece, exchange.piece.move(exchange.square));
        this.before = before;
        this.exchange = exchange;
    }

    static Stream<CaptureVariantProblem> findProblems(Piece piece) {
        return CaptureProblem.findProblem(piece.getExchange()).
                map(p -> p.getVariants()).
                orElse(Stream.empty());
    }

    // TODO it's not fair
    //  but getting all the problems is overkill
    static CaptureVariantProblem findProblem(Exchange exchange) {
        return CaptureProblem.findProblem(exchange).
                map(e -> e.getVariants().findAny().orElse(null)).
                orElse(null);
    }

    @Override
    public CaptureProblemSolver solve() {
        return new CaptureProblemSolver(this);
    }

    Solution solves(Problem problem) {
        return new Solution("Capture", move, problem);
    }

    @Override
    public int getScore() {
        return exchange.getScore();
    }

    @Override
    public String toString() {
        return "Capture " + piece + " => " + exchange;
    }
}
