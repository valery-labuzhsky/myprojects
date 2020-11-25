package board.situation;

import board.pieces.Piece;

import java.util.Objects;

/**
 * Created on 25.11.2020.
 *
 * @author unicorn
 */
public abstract class Problem {
    final Piece piece;

    public Problem(Piece piece) {
        this.piece = piece;
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
        return piece.equals(problem.piece);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece);
    }
}
