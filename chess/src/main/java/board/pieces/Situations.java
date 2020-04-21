package board.pieces;

import board.Waypoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situations {
    public final ArrayList<Solution> moves = new ArrayList<>();

    public final HashSet<Waypoint> waypoints = new HashSet<>();
    public final ArrayList<Solution> solutions = new ArrayList<>();

    private final Board board;
    public Situation check;
    public int score;
    private int defenceScore;

    public Situations(Board board) {
        this.board = board;
    }

    int result() {
        return this.score + this.getDefenceScore();
    }

    void addSolution(Waypoint waypoint) {
        if (waypoints.add(waypoint)) {
            this.solutions.add(new Solution(waypoint));
        }
    }

    // TODO I must have negative situations score
    //  positive situations will be part of solutions
    //  then I check all the negative situations and find solutions for them
    //  if there are no negative situations I check all the moves
    //  then I choose the best based on negative score
    //  then I choose the best based on positive score
    public void lookAt(Piece piece) {
        for (Waypoint waypoint : piece.square.waypoints) {
            if (waypoint.captures()) {
                Situation situation = new Situation(this, piece, this.board.color);
                if (piece.type == PieceType.King) {
                    check = situation;
                }
                score += situation.score;
                break;
            }
        }
    }

    public boolean isCheckmate() {
        return check != null && result() < -PieceType.King.score / 2;
    }

    public List<Solution> getMoves() {
        return this.moves;
    }

    public void analyse() {
        solutions.sort(Comparator.reverseOrder());

        Solution best = null;

        for (Solution solution : solutions) {
            if (solution.defence > 0) {
                if (best == null) {
                    best = solution;
                    defenceScore = solution.defence;
                } else if (best.compareTo(solution) > 0) {
                    break;
                }
                moves.add(solution);
            }
        }

        System.out.println("Total: " + score);
        System.out.println("Defend: " + getDefenceScore() + " " + moves);
    }

    public int getDefenceScore() {
        return defenceScore;
    }
}
