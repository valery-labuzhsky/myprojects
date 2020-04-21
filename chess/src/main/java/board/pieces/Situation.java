package board.pieces;

import board.Attack;
import board.Square;
import board.Waypoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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
    int bestScore;
    HashSet<Solution> solutions = new HashSet<>(); // TODO I must add solutions right to situations themselves

    public Situation(Piece piece, int color) {
        this.square = piece.square;
        solve(color);
    }

    private void solve(int color) {
        Piece piece = this.square.piece;
        if (piece.color != color) { // I'm capturing
            solvePositive();
        } else { // somebody attacks me
            this.score = piece.getScore();
            solveNegative();
        }
    }

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
        for (Attack guard : this.square.attacks.values()) { // guard
            if (guard.guards() && guard.through.moves()) {
                addSolution(guard.through);
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
                    if (block.piece != piece && block.piece.color == piece.color && block.moves()) {
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
        addSolution(waypoint, waypoint.getScore());
    }

    private void addSolution(Waypoint waypoint, int score) {
        if (this.solutions.add(new Solution(this, waypoint, score))) {
            if (score > this.bestScore) {
                this.bestScore = score;
            }
        }
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
