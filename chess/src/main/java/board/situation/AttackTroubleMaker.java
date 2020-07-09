package board.situation;

import board.Logged;
import board.exchange.DiffMoveScore;
import board.pieces.Piece;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created on 07.07.2020.
 *
 * @author unicorn
 */
public abstract class AttackTroubleMaker {
    protected final Piece piece;
    final ArrayList<AfterMoveScore> attacks;

    public AttackTroubleMaker(Piece piece) {
        this.piece = piece;

        ArrayList<AfterMoveScore> attacks = new ArrayList<>();
        ArrayList<Piece> enemies = new ArrayList<>(piece.board.pieces.get(-piece.color));
        for (Piece enemy : enemies) {
            enemy.getAttacks(piece.square).forEach(m -> {
                AfterMoveScore attack = new AfterMoveScore(piece, m, diff());
                if (attack.getScore() * m.piece.color > 0) {
                    attacks.add(attack);
                }
            });
        }

        // TODO add capturing of attacking piece

        this.attacks = Situations.best(attacks, DiffMoveScore::getScore, -piece.color);
    }

    protected abstract Function<Piece, ScoreWatcher> diff();

    public int getScore() {
        return attacks.stream().map(DiffMoveScore::getScore).findAny().orElse(0);
    }

    @Override
    public String toString() {
        return "" + piece + " " + Logged.tabs(attacks);
    }
}
