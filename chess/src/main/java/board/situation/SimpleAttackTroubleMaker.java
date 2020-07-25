package board.situation;

import board.exchange.ComplexExchange;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 07.07.2020.
 *
 * @author unicorn
 */
public class SimpleAttackTroubleMaker {
    static ArrayList<AttackProblem> findProblems(Piece piece) {
        ArrayList<AttackProblem> problems = new ArrayList<>();
        for (Piece enemy : piece.enemies()) {
            enemy.planAttacks(piece.square).forEach(m -> {
                if (m.to.scores.getExchange(enemy.color).move(enemy).getScore() * enemy.color >= 0) {
                    // TODO attack trouble maker must viable attacks despite of possible solutions
                    //  I must add solutions later and assess my problems again later
                    //  I will make more problems from problems with solutions
                    //  they will have different scores
                    //  therefore I need to rewrite OppositeAttack to work with existing attacks
                    AttackProblem attack = new AttackProblem(piece, m, ComplexExchange.diff(piece));
                    if (attack.getScore() * m.piece.color > 0) {
                        problems.add(attack);
                    }
                }
            });
        }
        return problems;
    }

}
