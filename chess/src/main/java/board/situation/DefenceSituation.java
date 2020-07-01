package board.situation;

import board.Waypoint;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class DefenceSituation extends Situation {

    DefenceSituation(Piece piece) {
        super(piece, piece.color);
        solve();
    }

    private void solve() {
        // somebody attacks me
        if (score() < 0) {
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

    public int score() {
        return exchange.result.score;
    }
}
