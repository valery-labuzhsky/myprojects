package board.situation;

import board.Move;
import board.Square;
import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.ArrayList;

import static board.Logged.tabs;

/**
 * Created on 30.06.2020.
 *
 * @author unicorn
 */
// TODO remove me once no variants are in use
public abstract class Variants extends ProblemSolver {
    protected final Piece piece;
    public final Square square;
    protected final Exchange mainExchange;
    final ArrayList<SamePiecesMoveScore> variants = new ArrayList<>();
    final CaptureProblem problem;

    // TODO use it fairly
    Variants(CaptureProblem problem) {
        this.problem = problem;
        Exchange exchange = problem.piece.getExchange();
        this.mainExchange = exchange;
        this.piece = exchange.piece;
        this.square = exchange.square;
    }

    void addSolution(Move move) {
        variants.add(new SamePiecesMoveScore(move));
        getSolutions().add(new Solution(move, problem));
    }

    public String toString() {
        return "" + mainExchange + tabs(getClass().getSimpleName(), variants);
    }

}
