package board.situation;

import board.Move;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 05.07.2020.
 *
 * @author unicorn
 */
public class Solution {
    final Move move;
    Problem problem;
    private final String name;

    @Deprecated
    Solution(Move move, Problem problem) {
        this("You name it", move, problem);
    }

    Solution(String name, Move move, Problem problem) {
        this.move = move;
        this.problem = problem;
        this.name = name;
    }

    public WhyNot whyNot() {
        return new WhyNot(whyNots().collect(Collectors.toList()));
    }

    public Stream<Analytics> whyNots() {
        return Stream.concat(move.trap(), move.piece.meaningfulRoles());
    }

    @Override
    public String toString() {
        return move.piece + " " + name + " to " + move.to + whyNot();
    }

}
