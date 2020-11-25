package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.LinkedList;
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
        Exchange exchange = piece.getExchange();
        if (exchange.getScore(piece) < 0) {
            CaptureProblem capture = new CaptureProblem(piece, exchange);
            return capture.getVariants();
        }
        return Stream.empty();
    }

    // TODO it's not fair
    //  but getting all the problems is overkill
    static CaptureVariantProblem findProblem(Exchange exchange) {
        Piece piece = exchange.piece;
        CaptureProblem before = new CaptureProblem(piece, exchange);
        LinkedList<Piece> enemies = exchange.sides.get(-piece.color).pieces;
        if (enemies.isEmpty()) return null;
        Piece enemy = enemies.get(0);
        Exchange move = exchange.move(enemy);
        if (move.getScore(piece) < 0) {
            return new CaptureVariantProblem(piece, before, move);
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
