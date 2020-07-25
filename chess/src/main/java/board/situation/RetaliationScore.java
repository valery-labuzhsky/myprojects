package board.situation;

import board.Logged;
import board.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 27.06.2020.
 *
 * @author unicorn
 */
public class RetaliationScore {
    final Move myMove;
    private final ArrayList<AttackProblem> worst;

    public RetaliationScore(Move myMove) {
        this.myMove = myMove;

        this.myMove.imagine();
        List<AttackProblem> problems = AfterEscapePieceScore.findProblems(myMove.piece).collect(Collectors.toList());
        this.myMove.undo();

        worst = Situations.best(problems, p -> p.getScore(), -myMove.piece.color);
    }

    public int getScore() {
        return worst.stream().map(a -> a.getScore()).findAny().orElse(0);
    }

    @Override
    public String toString() {
        return "" + myMove + " " + Logged.tabs(worst);
    }

}
