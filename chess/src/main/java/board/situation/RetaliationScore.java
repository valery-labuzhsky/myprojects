package board.situation;

import board.Logged;
import board.Move;
import board.exchange.DiffMoveScore;
import board.pieces.Piece;

import java.util.ArrayList;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class RetaliationScore {
    final Move myMove;
    private final ArrayList<AttackScore> retaliations;

    public RetaliationScore(Move myMove) {
        this.myMove = myMove;
        this.myMove.imagine();

        ArrayList<AttackScore> attacks = new ArrayList<>();

        ArrayList<Piece> enemies = new ArrayList<>(myMove.piece.board.pieces.get(-myMove.piece.color));
        for (Piece enemy : enemies) {
            enemy.getAttacks(myMove.piece.square).forEach(m -> attacks.add(new AttackScore(m, SituationScore::diff)));
        }

        attacks.forEach(MoveScore::calculate);

        retaliations = Situations.best(attacks, DiffMoveScore::getScore, -myMove.piece.color);

        this.myMove.undo();
    }

    public int getScore() {
        return retaliations.stream().map(DiffMoveScore::getScore).findAny().orElse(0);
    }

    @Override
    public String toString() {
        return "" + myMove + " " + Logged.tabs(retaliations);
    }
}
