package board.situation;

import board.Move;

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

    Solution fitMeToo(Problem problem) {
        return new Solution(name, move, problem);
    }

    @Override
    public String toString() {
        return name + " " + move.toString();
    }

}
