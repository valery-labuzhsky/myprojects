package board.pieces;

import board.Waypoint;

import java.util.Objects;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Solution {
    public final Situation situation;
    public final Waypoint waypoint;
    public final int score;

    public Solution(Situation situation, Waypoint waypoint, int score) {
        this.situation = situation;
        this.waypoint = waypoint;
        this.score = score;
    }

    public String toString() {
        return waypoint.toString();
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
        return waypoint.equals(solution.waypoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(waypoint);
    }
}
