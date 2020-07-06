package board.situation;

import board.Logged;
import board.exchange.DiffMoveScore;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 03.07.2020.
 *
 * @author unicorn
 */
public class OppositeAttacksNoEscapePieceScore {
    private final Piece piece;
    final ArrayList<AfterMoveScore> attacks;

    public OppositeAttacksNoEscapePieceScore(Piece piece) {
        this.piece = piece;

        ArrayList<AfterMoveScore> attacks = new ArrayList<>();
        ArrayList<Piece> enemies = new ArrayList<>(piece.board.pieces.get(-piece.color));
        for (Piece enemy : enemies) {
            enemy.getAttacks(piece.square).forEach(m -> {
                AfterMoveScore attack = new AfterMoveScore(piece, m, AfterEscapePieceScore::diff);
                if (attack.getScore() * m.piece.color > 0) {
                    attacks.add(attack);
                }
            });
        }

        this.attacks = Situations.best(attacks, DiffMoveScore::getScore, -piece.color);
    }

    public int getScore() {
        return attacks.stream().map(DiffMoveScore::getScore).findAny().orElse(0);
    }

    @Override
    public String toString() {
        return "" + piece + " " + Logged.tabs(attacks);
    }

}
