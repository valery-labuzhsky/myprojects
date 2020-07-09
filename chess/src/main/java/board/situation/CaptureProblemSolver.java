package board.situation;

import board.Waypoint;
import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class CaptureProblemSolver extends Variants {

    // TODO it's not fair
    @Deprecated
    static CaptureProblem createProblem(Exchange exchange) {
        ArrayList<Piece> enemies = exchange.sides.get(-exchange.piece.color).pieces;
        if (enemies.isEmpty()) return null;
        Piece enemy = enemies.get(0);
        return new CaptureProblem(exchange.piece, exchange.move(enemy));
    }

    CaptureProblemSolver(CaptureProblem problem) {
        super(problem);
        solve();
    }

    private void solve() {
        // somebody attacks me
        if (getScore() < 0) { // TODO it won't be necessary once I use trouble maker everywhere
            for (Waypoint waypoint : piece.getWaypoints()) { // escape
                if (waypoint.moves()) {
                    addSolution(waypoint.move());
                }
            }

            ArrayList<Waypoint> dangers = new ArrayList<>(); // gather all the villains
            for (Waypoint waypoint : this.square.waypoints) {
                if (waypoint.captures()) {
                    dangers.add(waypoint);
                }
            }

            int attackCost = dangers.stream().map(w -> w.piece.type.score).min(Integer::compareTo).orElse(0);
            if (attackCost > piece.type.score) {
                ArrayList<Piece> pieces = new ArrayList<>(square.board.pieces.get(piece.color)); // TODO it is changed
                for (Piece guard : pieces) { // guard
                    if (guard != piece) {
                        guard.getAttackSquares(piece.square).forEach(
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
    }

    @Override
    public int getScore() {
        return mainExchange.getResult().score;
    }

    CaptureProblemSolver counterAttacks(List<AfterMoveScore> attacks) {
        // TODO I'm a copy
        int myColor = piece.color;
        for (AfterMoveScore attack : attacks) {
            if (attack.getScore() * myColor >= getScore() * myColor) {
//                if (this.attack.move.piece != attack.piece) { // TODO add me back when move is added to the mixture
                getSolutions().add(new Solution(attack.move, problem));
//                }
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return "Capture " + super.toString();
    }
}
