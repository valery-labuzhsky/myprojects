package board.situation;

import board.Move;
import board.pieces.Piece;

import java.util.Objects;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public abstract class Problem {
    final Piece piece;
    final Move move;

    public Problem(Piece piece, Move move) {
        this.piece = piece;
        this.move = move;
    }

    public abstract ProblemSolver solve();

    public abstract int getScore();

    boolean worthIt() {
        return getScore() * piece.color < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return piece.equals(problem.piece) &&
                move.equals(problem.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, move);
    }
}
