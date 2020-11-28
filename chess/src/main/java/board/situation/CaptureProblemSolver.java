package board.situation;

import board.Move;
import board.exchange.Exchange;
import board.pieces.Piece;
import board.pieces.RayPiece;

import java.util.ArrayList;
import java.util.Iterator;

import static board.Logged.tabs;

/**
 * Created on 16.04.2020.
 *
 * @author ptasha
 */
public class CaptureProblemSolver extends ProblemSolver {

    CaptureProblemSolver(CaptureVariantProblem problem) {
        this(problem.before);
    }

    CaptureProblemSolver(CaptureProblem problem) {
        super(problem);
        solve();
    }

    public CaptureProblem getProblem() {
        return (CaptureProblem) problem;
    }

    private void solve() {
        Piece piece = problem.piece;

        piece.whereToMove().forEach(s -> addSolution("flees", piece.move(s)));

        piece.friends().forEach(
                guard -> {
                    Exchange exchange = getProblem().exchange;
                    Iterator<Move> iterator = guard.planAttacks(piece.square).iterator();
                    if (iterator.hasNext()) {
                        // TODO check if a guard is already guarding my piece already
                        //  mot only there, this check must probably go in why not section
                        int before = exchange.getScore(piece);
                        int after = exchange.add(guard).getScore(piece);
                        if (before < after) {
                            do {
                                Move move = iterator.next();
                                addSolution("guards", move);
                            } while (iterator.hasNext());
                        }
                    }
                }
        );

        getProblem().exchange.enemies(piece).forEach(enemy -> {
            if (enemy instanceof RayPiece) {
                piece.friends().forEach(friend -> friend.block(piece, enemy).forEach(m -> addSolution("blocks", m)));
            }
        });

    }

    @Override
    public int getScore() {
        return problem.getScore();
    }

    @Override
    void captures(ArrayList<CaptureVariantProblem> captures) {
        // TODO implement
    }

    public String toString() {
        return "" + problem + tabs(getClass().getSimpleName(), solutions);
    }
}
