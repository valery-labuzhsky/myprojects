package board.situation;

import board.Move;
import board.exchange.Exchange;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public class SimpleAttackProblem extends AttackProblem {
    public SimpleAttackProblem(Piece piece, Move move, Exchange then) {
        super(piece, move, then);
    }

    public Exchange then() {
        return (Exchange) analytics;
    }

    static ArrayList<SimpleAttackProblem> findProblems(Piece piece) {
        ArrayList<SimpleAttackProblem> problems = new ArrayList<>();
        for (Piece enemy : piece.enemies()) {
            enemy.planAttacks(piece.square).forEach(m -> {
                if (m.to.scores.getExchange(enemy.color).move(enemy).getScore(enemy) >= 0) {
                    Exchange now = piece.getExchange();
                    if (now.getScore(enemy) < piece.cost()) {
                        Exchange then = now.add(enemy);
                        if (then.getScore(enemy) >= 0) {
                            SimpleAttackProblem attack = new SimpleAttackProblem(piece, m, then);
                            if (attack.getScore() * m.piece.color > 0) {
                                problems.add(attack);
                            }
                        }
                    }
                }
            });
        }
        return problems;
    }

}
