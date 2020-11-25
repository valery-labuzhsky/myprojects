package board.situation;

import board.Move;
import board.pieces.Piece;

/**
 * Created on 29.07.2020.
 *
 * @author unicorn
 */
public abstract class AttackProblem extends MoveProblem {
    protected final Analytics analytics;

    public AttackProblem(Piece piece, Move move, Analytics analytics) {
        super(piece, move);
        this.analytics = analytics;
    }

    @Override
    public AttackProblemSolver solve() {
        return new AttackProblemSolver(this);
    }

    @Override
    public int getScore() {
        return analytics.getScore();
    }

    @Override
    public String toString() {
        return move.piece + " attacks " + piece + " through " + move.to + " = " + getScore() + analytics;
    }

}
