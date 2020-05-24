package board.situation;

import board.Logged;
import board.Move;
import board.Square;
import board.Waypoint;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Solution implements Comparable<Solution>, Logged {
    public static final Comparator<Solution> COMPARATOR = Comparator.<Solution>comparingInt(s -> s.defence).thenComparingInt(s -> s.attack);

    public final Move move;
    public int defence;
    private int attack;

    public Solution(Waypoint way) {
        this(way.move());
    }

    public Solution(Move move) {
        Piece piece = move.piece;
        Square to = move.to;

        this.move = move;

        HashSet<Piece> affected = new HashSet<>();
        for (Waypoint attack : piece.waypoints) { // whom I attack or guard
            if (attack.square.piece != null) {
                affected.add(attack.square.piece);
            }
        }

        for (Waypoint block : piece.square.waypoints) { // whom I block
            Piece blocked = block.getNearestPiece();
            if (blocked != null) {
                affected.add(blocked);
            }
        }

        piece.trace(to, s -> { // whom I will attack or guard
            Piece p = s.piece;
            if (p != null && p != piece) {
                affected.add(p);
                return false;
            }
            return true;
        });

        for (Waypoint block : to.waypoints) { // whom I will block
            Piece blocked = block.getNearestPiece();
            if (blocked != null && blocked != piece) {
                affected.add(blocked);
            }
        }

        if (to.piece != null) {
            for (Waypoint waypoint : to.piece.waypoints) { // whom he attack or guard
                if (waypoint.square.piece != null && waypoint.attacks()) {
                    affected.add(waypoint.square.piece);
                }
            }
        }

        this.defence -= move.from.board.score;
        this.defence -= piece.getScore();

        for (Piece p : affected) {
            if (p.color == move.color()) {
                defence -= p.getScore();
            } else {
                attack -= -p.getScore();
            }
        }

        move.imagine();

        this.defence += move.from.board.score;
        this.defence += piece.getScore();
        for (Piece p : affected) {
            if (p.color == move.color()) {
                defence += p.getScore();
            } else {
                attack += -p.getScore();
            }
        }

        move.undo();

        log().debug("Defence: " + defence + ", attack: " + attack);
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
