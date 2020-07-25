package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.stream.Stream;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public class CaptureProblem extends Problem {
    final Exchange exchange;

    public CaptureProblem(Piece piece, Exchange exchange) {
        super(piece, exchange.piece.move(exchange.square));
        this.exchange = exchange;
    }

    static Stream<CaptureProblem> findProblems(Piece piece) {
        Exchange exchange = piece.getExchange();
        if (exchange.getScore() * piece.color < 0) {
            return exchange.sides.get(-piece.color).pieces.stream().
                    map(enemy -> exchange.move(enemy)).
                    filter(problem -> problem.getScore() * piece.color < 0).
                    map(problem -> new CaptureProblem(piece, problem));
        }
        return Stream.empty();
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
