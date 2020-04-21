package board.pieces;

import board.FutureSquareExchange;
import board.Waypoint;

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
public class Solution implements Comparable<Solution> {
    public static final Comparator<Solution> COMPARATOR = Comparator.<Solution>comparingInt(s -> s.defence).thenComparingInt(s -> s.attack);

    public final Waypoint move;
    public int defence;
    private int attack;

    public Solution(Waypoint move) {
        this.move = move;
        this.defence = move.getScore();

        Function<Piece, Integer> pieceScore = p -> - -p.square.getScore(-p.color) * p.color * move.piece.color
                + -new FutureSquareExchange(p.square, -p.color, move).getScore() * p.color * move.piece.color;

        HashMap<Piece, Integer> affected = new HashMap<>();
        for (Waypoint attack : move.piece.waypoints) { // whom I attack or guard
            if (attack.square.piece != null) {
                affected.computeIfAbsent(attack.square.piece, pieceScore);
            }
        }

        for (Waypoint block : move.piece.square.waypoints) { // whom I block
            Piece piece = block.getNearestPiece();
            if (piece != null) {
                affected.computeIfAbsent(piece, pieceScore);
            }
        }

        move.piece.trace(move.square.pair, pair -> { // whom I will attack or guard
            Piece p = move.piece.board.getSquare(pair).piece;
            if (p != null && p != move.piece) {
                affected.computeIfAbsent(p, pieceScore);
                return false;
            }
            return true;
        });

        for (Waypoint block : move.square.waypoints) { // whom I will block
            Piece piece = block.getNearestPiece();
            if (piece != null && piece != move.piece) {
                affected.computeIfAbsent(piece, pieceScore);
            }
        }

        for (Map.Entry<Piece, Integer> entry : affected.entrySet()) {
            if (entry.getKey().color == move.piece.color) {
                defence += entry.getValue();
            } else {
                attack += entry.getValue();
            }
        }
        move.log().debug("Defence: " + defence + ", attack: " + attack);
    }

    public String toString() {
        return move.toString();
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
}
