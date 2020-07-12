package board.situation;

/**
 * Created on 09.07.2020.
 *
 * @author unicorn
 */
public class AttackProblem extends Problem {
    private final AfterMoveScore attack;

    public AttackProblem(AfterMoveScore attack) {
        super(attack.piece, attack.move);
        this.attack = attack;
    }

    @Override
    public int getScore() {
        return attack.getScore();
    }

    @Override
    public String toString() {
        return attack.toString();
    }
}
