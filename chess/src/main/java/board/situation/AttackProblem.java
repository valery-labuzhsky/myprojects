package board.situation;

import board.Move;
import board.pieces.Piece;

import java.util.stream.Stream;

import static board.Logged.tabs;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public class AttackProblem extends Problem {
    private final ScoreDiff diff;

    public AttackProblem(Piece piece, Move move, ScoreDiff diff) {
        super(piece, move);
        // TODO overkill
        new AfterMoveScore(piece, move, diff).calculate();
        this.diff = diff;
    }

    @Override
    public AttackProblemSolver solve() {
        return new AttackProblemSolver(this);
    }

    @Override
    public int getScore() {
        return diff.getScore();
    }

    @Override
    public String toString() {
        return "Attack " + move + " on " + piece + " = " + getScore() + tabs(Stream.of(diff));
    }
}
