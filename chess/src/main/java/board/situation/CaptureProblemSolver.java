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

        // TODO now I need checking squares I step into
        //  let's try to summarize algorithm here
        //  I can add if here, but I won't see it
        //  I need combine all the factors which relevant to the solution
        //  so I can print it out and use this information for ifs
        //  basically I need to have in memory logics so I can display it
        //  I may need some language to display it - good
        //  that's how simple problem becomes interesting solution :-)
        //  so everything boils down to some language and its processor

        // TODO
        //  Piece attacks Piece through square
        //   Piece flees to Square
        //    Trap for Piece on Square

        // TODO what if I don't want to change my code? how can I analyze my structures?
        //  an interesting thought
        //  I can get trace and store all the ifs which leads here
        //  but it's definitely another story

        // TODO so I need adding into account
        //  info on safety of a square to go to
        //  and role info
        piece.whereToMove().forEach(s -> addSolution("flees", piece.move(s)));

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
