package board.situation;

import board.Move;
import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.List;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public class CaptureProblem extends Problem {
    protected final Piece piece;
    private final Move move;
    private final Exchange exchange;

    public CaptureProblem(Piece piece, Exchange exchange) {
        this.piece = piece;
        this.move = exchange.piece.move(exchange.square);
        this.exchange = exchange;
    }

    public CaptureProblemSolver solve(List<AfterMoveScore> myAttacks) {
        return new CaptureProblemSolver(this).counterAttacks(myAttacks);
    }

    public Solution takeAdvantageOf() {
        return new Solution("Capture", move, this);
    }

    @Override
    public int getScore() {
        return exchange.getScore();
    }
}
