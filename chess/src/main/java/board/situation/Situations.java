package board.situation;

import board.Board;
import board.Waypoint;
import board.pieces.Piece;
import board.pieces.PieceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    public final Board board;
    public Situation check;
    public int score;
    private int defenceScore;

    public Situations(Board board) {
        this.board = board;
    }

    public int result() {
        return this.score + this.getDefenceScore();
    }

    void addSolution(Waypoint waypoint) {
        if (waypoints.add(waypoint)) {
            this.solutions.add(new Solution(waypoint));
        }
    }

    public void lookAt(Piece piece) {
        for (Waypoint waypoint : piece.square.waypoints) {
            if (waypoint.captures()) {
                Situation situation = new Situation(this, piece, this.board.color);
                log().info(situation);
                if (piece.type == PieceType.King) {
                    check = situation;
                }
                score += situation.score;
                break;
            }
        }

        piece.log().info(piece.square.getExchangeResult(-piece.color));

        // TODO gather future situations per attack at least for now
        //  first I estimate worthiness
        //  second I estimate the move itself
        // TODO try some countermeasures
        //  gather solutions
        //  estimate cost of initial move based on the countermeasure, that's our score
        // TODO does it worth to check every attack? some of them might be fruitless
        //  I need to have some score, having it and a piece I must be able to estimate worthiness of the attack
        //  I already thought about it it doesn't seem to be feasible

        // TODO I need do my game recursive as exchange to be able to do all those steps,
        //  actually I already have it, I can write all moves to history and revert them
        //  but to do it I must do moving pieces more lightweight, at least attacks must go away
        //  I'm not so sure about waypoints, but I could do special collections on the fly
    }

    public boolean isCheckmate() {
        return check != null && result() < -PieceType.King.score / 2;
    }

    public List<Solution> getMoves() {
        return this.moves;
    }

    public void analyse() {
        solutions.sort(Comparator.reverseOrder());
        log().info("solutions " + solutions);

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

    private Logger log() {
        return LogManager.getLogger("situations");
    }

    public int getDefenceScore() {
        return defenceScore;
    }
}
