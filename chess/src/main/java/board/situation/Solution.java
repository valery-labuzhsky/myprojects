package board.situation;

import board.Logged;
import board.Move;
import board.Square;
import board.Waypoint;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Solution implements Comparable<Solution>, Logged {
    public static final Comparator<Solution> COMPARATOR = Comparator.<Solution>comparingInt(s -> s.defence).thenComparingInt(s -> s.attack);

    public final Move move;
    public final int defence;
    private final int attack;

    public Solution(Waypoint way) {
        this(way.move());
    }

    public Solution(Move move) {
        this.move = move;

        SolutionWatcher watcher = new SolutionWatcher(move);
        watcher.calculate();

        defence = watcher.defence.score;
        attack = watcher.attack.score;

        log().debug("Defence: " + defence + ", attack: " + attack);
    }

    public static class SolutionWatcher extends MoveWatcher {
        final ScoreWatcher defence = new ScoreWatcher();
        final ScoreWatcher attack = new ScoreWatcher();

        public SolutionWatcher(Move move) {
            super(move);
        }

        @Override
        public void collectBefore() {
            Piece piece = move.piece;
            defence.collect(piece.board);
            defence.collect(piece);
            collect(piece.whomAttack()); // whom I attack
            collect(piece.whomBlock()); // whom I block

            // TODO I don't need this for exchange
            Square to = move.to;
            if (to.piece != null) {
                exclude(to.piece);
                collect(to.piece.whomAttack()); // whom he attack or guard
            }
        }

        @Override
        public void collectAfter() {
            Piece piece = move.piece;
            collect(piece.whomAttack()); // whom I will attack
            collect(piece.whomBlock()); // whom I will block
        }

        @Override
        public void calculateBefore() {
            this.defence.before();
            this.attack.before();
        }

        @Override
        public void calculateAfter() {
            this.defence.after();
            this.attack.after();
        }

        public void exclude(Piece piece) {
            attack.exclude(piece);
        }

        @Override
        public void collect(Piece piece) {
            if (color == piece.color) {
                defence.collect(piece);
            } else {
                attack.collect(piece);
            }
        }
    }

    public String toString() {
        return "" + move + "+" + defence + "-" + attack;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Solution solution = (Solution) o;
        return move.equals(solution.move);
    }

    @Override
    public int hashCode() {
        return Objects.hash(move);
    }

    @Override
    public int compareTo(Solution solution) {
        return COMPARATOR.compare(this, solution);
    }

    @Override
    public Logger log() {
        return move.log();
    }
}
