package board.pieces;

import board.Attack;
import board.Square;
import board.Waypoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situation {
    Square square;
    int score;
    int bestScore = Integer.MIN_VALUE;
    ArrayList<Solution> solutions = new ArrayList<>();

    public Situation(Piece piece, int color) {
        this.square = piece.square;
        solve(color);
    }

    private void solve(int color) {
        Piece piece = this.square.piece;
        if (piece.color != color) { // I'm capturing
            solvePositive();
        } else { // somebody attacks me
            this.score = -piece.type.score;
            solveNegative();
        }
    }

    // TODO review all the ifs and methods used
    private void solveNegative() {
        Piece piece = this.square.piece;
        for (Waypoint waypoint : piece.waypoints) { // escape
            if (!waypoint.square.captures(piece) && waypoint.moves()) {
                addSolution(waypoint);
            }
        }
        ArrayList<Waypoint> dangers = new ArrayList<>(); // gather all the villains
        for (Waypoint waypoint : this.square.waypoints) {
            if (waypoint.captures()) {
                dangers.add(waypoint);
            }
        }
        // TODO let's estimate exchanges realistically
        for (Attack guard : this.square.attacks) { // guard
            if (guard.guards() && guard.through.moves()) {
                int score = guard.getScore();
                if (score > 0) {
                    score = 0;
                }
                addSolution(guard.through, -this.score + score);
            }
        }
        if (dangers.size() == 1) {
            Waypoint danger = dangers.get(0);
            for (Waypoint waypoint : danger.piece.square.waypoints) { // kill him
                if (waypoint.captures(danger.piece)) {
                    addSolution(waypoint);
                }
            }
            while (danger.prev != null) { // block
                danger = danger.prev;
                for (Waypoint block : danger.square.waypoints) {
                    if (block.piece.color == piece.color && block.moves()) {
                        addSolution(block);
                    }
                }
            }
        }
    }

    private void solvePositive() {
        for (Waypoint waypoint : this.square.waypoints) {
            if (waypoint.captures()) {
                addSolution(waypoint);
            }
        }
    }

    private void addSolution(Waypoint waypoint) {
        addSolution(waypoint, -this.score + waypoint.getScore());
    }

    private void addSolution(Waypoint waypoint, int score) {
        if (score > this.bestScore) {
            this.bestScore = score;
        }
        this.solutions.add(new Solution(this, waypoint, score));
    }

    void addSolutions(Collection<Waypoint> solutions) {
        for (Solution solution : this.solutions) {
            if (solution.score == bestScore) {
                solutions.add(solution.waypoint);
            }
        }
    }

    public String toString() {
        return "" + score + " x " + (score + bestScore) + ": " + square.pair + " " + square + " " + printSolutions();
    }

    private String printSolutions() {
        return "{" + solutions.stream().
                filter(s -> s.score == bestScore).map(Objects::toString).
                collect(Collectors.joining(", ")) + "}";
    }

}
