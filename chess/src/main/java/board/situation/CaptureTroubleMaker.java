package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created on 08.07.2020.
 *
 * @author unicorn
 */
class CaptureTroubleMaker {
    final ArrayList<CaptureProblem> problems = new ArrayList<>();

    public CaptureTroubleMaker(Piece piece) {
        Exchange exchange = piece.getExchange();
        if (exchange.getScore() * piece.color < 0) {
            for (Piece enemy : exchange.sides.get(-piece.color).pieces) {
                Exchange problem = exchange.move(enemy);
                if (problem.getScore() * piece.color < 0) {
                    problems.add(new CaptureProblem(piece, exchange));
                }
            }
        }
    }

    Stream<CaptureProblemSolver> makeProblems(List<AfterMoveScore> myAttacks) {
        return problems.stream().map(p -> p.solve(myAttacks));
    }

    public Stream<Solution> takeAdvantageOf() {
        return problems.stream().map(p -> p.takeAdvantageOf());
    }

}
