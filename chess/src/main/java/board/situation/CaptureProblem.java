package board.situation;

import board.exchange.Exchange;
import board.pieces.Piece;

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

    public CaptureProblemSolver solve() {
        return new CaptureProblemSolver(this);
    }

    public Solution takeAdvantageOf() {
        return new Solution("Capture", move, this);
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
