package board.situation;

import board.Waypoint;
import board.pieces.Piece;

import java.util.ArrayList;

import static board.Logged.tabs;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class CaptureProblemSolver extends ProblemSolver {

    CaptureProblemSolver(CaptureProblem problem) {
        super(problem);
        solve();
    }

    private void solve() {
        Piece piece = problem.piece;
        piece.whereToMove().forEach(s -> addSolution(piece.move(s)));

        ArrayList<Waypoint> dangers = new ArrayList<>(); // gather all the villains
        for (Waypoint waypoint : piece.square.waypoints) {
            if (waypoint.captures()) {
                dangers.add(waypoint);
            }
        }

        int attackCost = dangers.stream().map(w -> w.piece.type.score).min(Integer::compareTo).orElse(0);
        if (attackCost > piece.type.score) {
            for (Piece guard : piece.friends()) { // guard
                if (guard != piece) {
                    guard.planAttackSquares(piece.square).forEach(
                            s -> addSolution(guard.move(s)));
                }
            }
        }

        if (dangers.size() == 1) {
            Waypoint danger = dangers.get(0);
            while (danger.prev != null) { // block
                danger = danger.prev;
                for (Waypoint block : danger.square.getWaypoints()) {
                    if (block.piece != piece && block.piece.color == piece.color && block.moves()) {
                        addSolution(block.move());
                    }
                }
            }
        }
    }

    @Override
    public int getScore() {
        return problem.getScore();
    }

    public String toString() {
        return "" + problem + tabs(getClass().getSimpleName(), solutions);
    }
}
