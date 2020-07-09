package board.situation;

import board.Logged;
import board.Move;
import board.pieces.Piece;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class RetaliationScore {
    final Move myMove;
    private final OppositeAttacksNoEscapeTroubleMaker score;

    public RetaliationScore(Move myMove) {
        this.myMove = myMove;
        this.myMove.imagine();

        Piece piece = myMove.piece;

        score = new OppositeAttacksNoEscapeTroubleMaker(piece);

        this.myMove.undo();
    }

    public int getScore() {
        return score.getScore();
    }

    @Override
    public String toString() {
        return "" + myMove + " " + Logged.tabs(score.attacks);
    }

}
