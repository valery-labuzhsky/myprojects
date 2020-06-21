package board.situation;

import board.Logged;
import board.Move;
import board.Square;
import board.Waypoint;
import board.pieces.Piece;
import board.pieces.ScoreProvider;
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
    private final SolutionWatcher watcher;

    public Solution(Waypoint way) {
        this(way.move());
    }

    public Solution(Move move) {
        this.move = move;

        watcher = new SolutionWatcher(move);
        watcher.calculate();

        defence = watcher.defence.getScore();
        attack = watcher.attack.getScore();

        log().debug("Defence: " + defence + ", attack: " + attack);
    }

    public static class SolutionWatcher extends MoveWatcher<Move> {
        final ListScoreWatcher defence = new ListScoreWatcher();
        final ListScoreWatcher attack = new ListScoreWatcher();
        final int color;

        public SolutionWatcher(Move move) {
            super(move);
            Piece piece = move.piece;
            this.color = piece.color;
        }

        @Override
        public void collectBefore() {
            Piece piece = move.piece;
            defence.collect(piece);
            collect(piece.whomAttack()); // whom I attack
            collect(piece.whomBlock()); // whom I block

            // TODO I don't need this for exchange
            Square to = move.to;
            if (to.piece != null) {
                defence.collect(new CaptureScore(to.piece));
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

        @Override
        public void finish() {
            defence.calculate();
            attack.calculate();
        }

        public void exclude(Piece piece) {
            attack.exclude(piece);
        }

        @Override
        public void collect(ScoreProvider piece) {
            if (color == ((Piece) piece).color) {
                defence.collect(piece);
            } else {
                attack.collect(piece);
            }
        }

        public String toString() {
            // TODO I need all the participants and their score
            //  I need calculating not only score but structure as well
            //  It will hold score before and then diff
            return "+" + defence + " -" + attack;
        }

    }

    public String toString() {
        return "" + move + ": " + watcher;
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
    public Logger getLogger() {
        return move.getLogger();
    }

}
