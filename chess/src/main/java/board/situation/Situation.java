package board.situation;

import board.Move;
import board.Square;
import board.Waypoint;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class Situation {
    private final Situations situations;
    Square square;
    int score;

    public Situation(Situations situations, Piece piece, int color) {
        this.situations = situations;
        this.square = piece.square; // TODO should I use piece instead?
        solve(color);
    }

    private void solve(int color) {
        Piece piece = this.square.piece;
        if (piece.color != color) { // I'm capturing
            solvePositive();
        } else { // somebody attacks me
            this.score = piece.getScore();
            if (score < 0) {
                solveNegative();
            }
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

        for (Piece guard : situations.board.pieces.get(piece.color)) { // guard
            if (guard != piece) {
                guard.getAttacks(piece.square).forEach(
                        s -> addSolution(guard.move(s)));
            }
        }

        if (dangers.size() == 1) {
            Waypoint danger = dangers.get(0);
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
        situations.addSolution(waypoint);
    }

    private void addSolution(Move move) {
        situations.addSolution(move);
    }

    public String toString() {
        return "" + square + ": " + score;
    }
}
