package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.ArrayList;
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
        if (exchange.getScore(piece) < 0) {
            // TODO all these problems are hypothetical ones
            //  single escape move solves all of them
            return exchange.sides.get(-piece.color).pieces.stream().
                    map(enemy -> exchange.move(enemy)).
                    filter(problem -> problem.getScore(piece) < 0).
                    map(problem -> new CaptureProblem(piece, problem));
        }
        return Stream.empty();
    }

    // TODO it's not fair
    //  but getting all the problems is overkill
    static CaptureProblem findProblem(Exchange exchange) {
        ArrayList<Piece> enemies = exchange.sides.get(-exchange.piece.color).pieces;
        if (enemies.isEmpty()) return null;
        Piece enemy = enemies.get(0);
        Exchange move = exchange.move(enemy);
        if (move.getScore() * exchange.piece.color < 0) {
            return new CaptureProblem(exchange.piece, move);
        } else {
            return null;
        }
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
