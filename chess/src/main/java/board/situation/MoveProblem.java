package board.situation;

import board.Move;
import board.pieces.Piece;

import java.util.Objects;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public abstract class MoveProblem extends Problem {
    final Move move;

    public MoveProblem(Piece piece, Move move) {
        super(piece);
        this.move = move;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MoveProblem problem = (MoveProblem) o;
        return move.equals(problem.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), move);
    }
}
