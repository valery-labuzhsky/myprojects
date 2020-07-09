package board.situation;

import board.pieces.Piece;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created on 03.07.2020.
 *
 * @author unicorn
 */
public class OppositeAttacksNoEscapeTroubleMaker extends AttackTroubleMaker {

    public OppositeAttacksNoEscapeTroubleMaker(Piece piece) {
        super(piece);
    }

    @Override
    protected Function<Piece, ScoreWatcher> diff() {
        return AfterEscapePieceScore::diff;
    }

    Stream<AttackProblemSolver> makeProblems(ArrayList<AfterMoveScore> counterAttacks) {
        return attacks.stream().
                map(a -> new AttackProblemSolver(a).counterAttacks(counterAttacks));
    }
}
