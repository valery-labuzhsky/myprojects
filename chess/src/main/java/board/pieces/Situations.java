package board.pieces;

import board.Waypoint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situations {
    public final ArrayList<Situation> situations = new ArrayList<>();
    public final ArrayList<Waypoint> solutions = new ArrayList<>();
    private final Board board;
    public Situation check;
    public int totalScore;
    public int bestScore;

    public Situations(Board board) {
        this.board = board;
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
                Situation situation = new Situation(piece, this.board.color);
                if (piece.type == PieceType.King) {
                    check = situation;
                }
                situations.add(situation);
                break;
            }
        }
    }

    public boolean isCheckmate() {
        return check != null && totalScore - bestScore < -PieceType.King.score / 2;
    }

    public List<Waypoint> getMoves() {
        return this.solutions;
    }

    public void analyse() {
        this.situations.sort(Comparator.comparingInt(s -> -s.bestScore));

        for (Situation situation : situations) {
            totalScore += situation.score;
            if (situation.bestScore >= bestScore && situation.bestScore > 0) {
                if (situation.bestScore > bestScore) {
                    solutions.clear();
                }
                bestScore = situation.bestScore;
                situation.addSolutions(solutions);
            }
        }

        System.out.println("Total: " + totalScore);
        System.out.println("Best: " + bestScore + " " + solutions);
    }
}
