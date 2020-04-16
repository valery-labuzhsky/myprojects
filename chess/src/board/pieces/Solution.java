package board.pieces;

import board.Waypoint;

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
}
