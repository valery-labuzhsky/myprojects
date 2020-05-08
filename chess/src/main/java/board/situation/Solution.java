package board.situation;

import board.Logged;
import board.Move;
import board.Square;
import board.Waypoint;
import board.exchange.FutureSquareExchange;
import board.exchange.WaypointExchange;
import board.pieces.Piece;
import org.apache.logging.log4j.Logger;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

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
        Piece piece = move.piece();
        Square to = move.to;

        this.move = move;

        this.defence = getScore(move);

        Function<Piece, Integer> pieceScore = p -> - -p.square.getScore(-p.color) * p.color * move.color()
                + -new FutureSquareExchange(p.square, -p.color, move).getScore() * p.color * move.color();

        HashMap<Piece, Integer> affected = new HashMap<>();
        for (Waypoint attack : piece.waypoints) { // whom I attack or guard
            if (attack.square.piece != null) {
                affected.computeIfAbsent(attack.square.piece, pieceScore);
            }
        }

        for (Waypoint block : piece.square.waypoints) { // whom I block
            Piece blocked = block.getNearestPiece();
            if (blocked != null) {
                affected.computeIfAbsent(blocked, pieceScore);
            }
        }

        piece.trace(to, s -> { // whom I will attack or guard
            Piece p = s.piece;
            if (p != null && p != piece) {
                affected.computeIfAbsent(p, pieceScore);
                return false;
            }
            return true;
        });

        for (Waypoint block : to.waypoints) { // whom I will block
            Piece blocked = block.getNearestPiece();
            if (blocked != null && blocked != piece) {
                affected.computeIfAbsent(blocked, pieceScore);
            }
        }

        if (to.piece != null) {
            for (Waypoint waypoint : to.piece.waypoints) { // whom he attack or guard
                if (waypoint.square.piece != null && waypoint.attacks()) {
                    affected.computeIfAbsent(waypoint.square.piece, pieceScore);
                }
            }
        }

        for (Map.Entry<Piece, Integer> entry : affected.entrySet()) {
            if (entry.getKey().color == move.color()) {
                defence += entry.getValue();
            } else {
                attack += entry.getValue();
            }
        }

        log().debug("Defence: " + defence + ", attack: " + attack);
    }

    private int getScore(Move move) {
        int waypointScore = new WaypointExchange(move).getScore();
        int squareScore = move.from.getScore(-move.color()); // TODO it's wrong, I need calculating difference between now and then

        log().debug(move + ": " + waypointScore + " + " + squareScore);

        return waypointScore + squareScore;
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
